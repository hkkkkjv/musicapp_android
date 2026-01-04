package ru.kpfu.itis.song.api

import ru.kpfu.itis.core.domain.models.Song

interface SearchRepository {
    suspend fun searchSongs(query: String): SearchResult
    suspend fun getSongById(id: String): Song
}