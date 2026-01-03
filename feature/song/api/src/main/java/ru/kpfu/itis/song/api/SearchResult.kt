package ru.kpfu.itis.song.api

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val query: String,
    val songs: List<Song>
)
