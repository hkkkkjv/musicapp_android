package ru.kpfu.itis.song.api

import kotlinx.serialization.Serializable
import ru.kpfu.itis.core.domain.models.Song

@Serializable
data class SearchResult(
    val query: String,
    val songs: List<Song>
)
