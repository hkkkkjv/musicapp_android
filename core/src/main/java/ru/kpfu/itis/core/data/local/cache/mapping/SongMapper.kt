package ru.kpfu.itis.core.data.local.cache.mapping

import ru.kpfu.itis.core.data.local.cache.entity.CachedSong
import ru.kpfu.itis.core.domain.models.Song

fun Song.toCachedSong() = CachedSong(
    id = id,
    title = title,
    artist = artist,
    album = album,
    coverUrl = coverUrl,
    previewUrl = previewUrl,
    source = source.name,
    geniusUrl = geniusUrl,
    releaseDate = releaseDate,
    durationSec = durationSec,
    popularity = popularity,
    lyrics = lyrics
)

fun CachedSong.toSong() = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    coverUrl = coverUrl,
    previewUrl = previewUrl,
    source = enumValueOf(source),
    geniusUrl = geniusUrl,
    releaseDate = releaseDate,
    durationSec = durationSec,
    popularity = popularity,
    lyrics = lyrics
)