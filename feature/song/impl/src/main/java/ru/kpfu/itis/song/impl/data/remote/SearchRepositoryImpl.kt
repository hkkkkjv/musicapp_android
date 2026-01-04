package ru.kpfu.itis.song.impl.data.remote

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.kpfu.itis.core.data.network.deezer.DeezerApi
import ru.kpfu.itis.core.data.network.genius.GeniusApi
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.api.SearchResult
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val geniusApi: GeniusApi,
    private val deezerApi: DeezerApi,
    private val htmlLyricsParser: HtmlLyricsParser
) : SearchRepository {

    override suspend fun searchSongs(query: String): SearchResult {
        return coroutineScope {
            val geniusDeferred = async {
                runCatching {
                    geniusApi.searchSongs(query)
                        .response
                        .hits
                        .map { hit ->
                            Song(
                                id = "genius:${hit.result.id}",
                                title = hit.result.title,
                                artist = hit.result.primaryArtist.name,
                                album = null,
                                coverUrl = hit.result.songArtImageUrl,
                                previewUrl = null,
                                source = SongSource.GENIUS,
                                geniusUrl = hit.result.url,
                                releaseDate = null,
                                durationSec = null,
                                popularity = null
                            )
                        }
                }.getOrElse {
                    Log.e("SearchRepo", "Genius API error: ${it.message}")
                    emptyList()
                }
            }

            val deezerDeferred = async {
                runCatching {
                    deezerApi.searchTracks(query)
                        .data
                        .map { track ->
                            Song(
                                id = "deezer:${track.id}",
                                title = track.title,
                                artist = track.artist.name,
                                album = track.album?.title,
                                coverUrl = track.album?.cover,
                                previewUrl = track.preview,
                                source = SongSource.DEEZER,
                                geniusUrl = null,
                                releaseDate = track.releaseDate,
                                durationSec = track.duration,
                                popularity = track.rank
                            )
                        }
                }.getOrElse {
                    Log.e("SearchRepo", "Deezer API error: ${it.message}")
                    emptyList()
                }
            }

            val geniusSongs = geniusDeferred.await()
            val deezerSongs = deezerDeferred.await()

            val merged = (geniusSongs + deezerSongs)
                .distinctBy { it.title.lowercase() + "|" + it.artist.lowercase() }
                .take(30)

            SearchResult(query = query, songs = merged)
        }
    }

    override suspend fun getSongById(id: String): Song {
        return when {
            id.startsWith("genius:") -> {
                val realId = id.removePrefix("genius:").toLong()
                val resp = geniusApi.getSongDetails(realId)
                val geniusUrl = resp.response.song.url
                val lyrics = try {
                    val response = geniusApi.getSongHtml(geniusUrl)
                    val html = response.string()
                    htmlLyricsParser.parseLyricsFromHtml(html)
                } catch (e: Exception) {
                    Log.e("SearchRepo", "Ошибка загрузки текста: ${e.message}")
                    null
                }
                Song(
                    id = "genius:${resp.response.song.id}",
                    title = resp.response.song.title,
                    artist = resp.response.song.primaryArtist.name,
                    album = null,
                    coverUrl = resp.response.song.songArtImageUrl,
                    previewUrl = null,
                    source = SongSource.GENIUS,
                    geniusUrl = resp.response.song.url,
                    releaseDate = null,
                    durationSec = null,
                    popularity = null,
                    lyrics = lyrics
                )
            }

            id.startsWith("deezer:") -> {
                val realId = id.removePrefix("deezer:").toLong()
                val resp = deezerApi.getTrack(realId)
                Song(
                    id = "deezer:${resp.id}",
                    title = resp.title,
                    artist = resp.artist.name,
                    album = resp.album?.title,
                    coverUrl = resp.album?.cover,
                    previewUrl = cleanPreviewUrl(resp.preview),
                    source = SongSource.DEEZER,
                    geniusUrl = null,
                    releaseDate = resp.releaseDate,
                    durationSec = resp.duration,
                    popularity = resp.rank
                )
            }

            else -> throw IllegalArgumentException()
        }
    }

    private fun cleanPreviewUrl(url: String?): String? {
        if (url == null) return null
        return url.replace("\\/", "/")
    }

}
