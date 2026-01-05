package ru.kpfu.itis.song.impl.data.remote

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import ru.kpfu.itis.core.data.local.cache.dao.CachedSongDao
import ru.kpfu.itis.core.data.local.cache.dao.SearchCacheDao
import ru.kpfu.itis.core.data.local.cache.entity.SearchCache
import ru.kpfu.itis.core.data.local.cache.mapping.toCachedSong
import ru.kpfu.itis.core.data.local.cache.mapping.toSong
import ru.kpfu.itis.core.data.network.deezer.DeezerApi
import ru.kpfu.itis.core.data.network.genius.GeniusApi
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource
import ru.kpfu.itis.core.utils.DatabaseConstants
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.api.SearchResult
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val geniusApi: GeniusApi,
    private val deezerApi: DeezerApi,
    private val htmlLyricsParser: HtmlLyricsParser,
    private val cachedSongDao: CachedSongDao,
    private val searchCacheDao: SearchCacheDao,
) : SearchRepository {

    override suspend fun searchSongs(query: String): SearchResult {
        val cacheKey = query.lowercase().trim()
        val cachedResult = searchCacheDao.getSearchCache(cacheKey)

        if (cachedResult != null && !isCacheExpired(cachedResult.cachedAt)) {
            Log.d("SearchRepo", "Cache hit for query: $query")

            val songIds = try {
                Json.decodeFromString<List<String>>(cachedResult.songIds)
            } catch (e: Exception) {
                emptyList()
            }

            val cachedSongs = cachedSongDao.getSongsByIds(songIds)
            if (cachedSongs.size == songIds.size) {
                return SearchResult(
                    query = query,
                    songs = cachedSongs.map { it.toSong() }
                )
            }
        }
        Log.d("SearchRepo", "Cache miss for query: $query - fetching from API")

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
                .partition { it.source == SongSource.GENIUS }
                .let { (genius, deezer) ->
                    val geniusMaxEach = 10
                    val deezerMaxEach = 15

                    val geniusResult = genius.take(geniusMaxEach)
                    val deezerResult = deezer.take(deezerMaxEach)

                    (geniusResult + deezerResult).take(30)
                }

            cacheSearchResults(cacheKey, merged)

            SearchResult(query = query, songs = merged)
        }
    }

    override suspend fun getSongById(id: String): Song {
        val cachedSong = cachedSongDao.getSongById(id)

        if (cachedSong != null && !isCacheExpired(cachedSong.cachedAt)) {
            Log.d("SearchRepo", "Cache hit for song: $id")
            if (id.startsWith("genius:")) {
                if (cachedSong.lyrics != null) {
                    Log.d("SearchRepo", "Found lyrics in cache for: $id")
                    return cachedSong.toSong()
                } else {
                    Log.d("SearchRepo", "No lyrics in cache for Genius song: $id - fetching lyrics")
                    return try {
                        val lyrics = fetchGeniusLyrics(cachedSong.geniusUrl!!)

                        val updatedCachedSong = cachedSong.copy(lyrics = lyrics)
                        cachedSongDao.insertSong(updatedCachedSong)
                        Log.d("SearchRepo", "Loaded and cached lyrics for: $id")

                        updatedCachedSong.toSong()
                    } catch (e: Exception) {
                        Log.e("SearchRepo", "Error loading text for Genius: ${e.message}")
                        cachedSong.toSong()
                    }
                }
            } else if (id.startsWith("deezer:")) {
                Log.d("SearchRepo", "Returning Deezer song from cache: $id")
                return cachedSong.toSong()
            }
            return cachedSong.toSong()
        }

        Log.d("SearchRepo", "Cache miss for song: $id - fetching from API")
        val song = when {
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
        cachedSongDao.insertSong(song.toCachedSong())

        return song
    }

    private suspend fun fetchGeniusLyrics(geniusUrl: String): String? {
        return try {
            Log.d("SearchRepo", "Fetching lyrics from: $geniusUrl")

            val response = geniusApi.getSongHtml(geniusUrl)
            val html = response.string()
            val lyrics = htmlLyricsParser.parseLyricsFromHtml(html)

            if (lyrics != null && lyrics.isNotBlank()) {
                Log.d("SearchRepo", "Successfully fetched ${lyrics.length} chars of lyrics")
                lyrics
            } else {
                Log.w("SearchRepo", "Lyrics are empty for: $geniusUrl")
                null
            }
        } catch (e: Exception) {
            Log.e("SearchRepo", "Error fetching Genius lyrics: ${e.message}")
            null
        }
    }


    private fun cleanPreviewUrl(url: String?): String? {
        if (url == null) return null
        return url.replace("\\/", "/")
    }

    private fun isCacheExpired(cachedAt: Long): Boolean {
        val now = System.currentTimeMillis()
        val age = now - cachedAt
        return age > DatabaseConstants.CACHE_DURATION_MS
    }

    private suspend fun cacheSearchResults(query: String, songs: List<Song>) {
        try {
            val cachedSongs = songs.map { it.toCachedSong() }
            cachedSongDao.insertSongs(cachedSongs)

            val songIds = Json.encodeToString(songs.map { it.id })
            searchCacheDao.insertSearchCache(
                SearchCache(
                    query = query,
                    songIds = songIds
                )
            )

            Log.d("SearchRepo", "Cached ${songs.size} songs for query: $query")
        } catch (e: Exception) {
            Log.e("SearchRepo", "Error caching search results: ${e.message}")
        }
    }

    suspend fun clearCache() {
        try {
            cachedSongDao.clearAll()
            searchCacheDao.clearAll()
            Log.d("SearchRepo", "Cache cleared")
        } catch (e: Exception) {
            Log.e("SearchRepo", "Error clearing cache: ${e.message}")
        }
    }

    suspend fun cleanExpiredCache() {
        try {
            val timeLimit = System.currentTimeMillis() - DatabaseConstants.CACHE_DURATION_MS
            cachedSongDao.deleteExpiredCache(timeLimit)
            searchCacheDao.deleteExpiredCache(timeLimit)
            Log.d("SearchRepo", "Expired cache cleaned")
        } catch (e: Exception) {
            Log.e("SearchRepo", "Error cleaning expired cache: ${e.message}")
        }
    }
}
