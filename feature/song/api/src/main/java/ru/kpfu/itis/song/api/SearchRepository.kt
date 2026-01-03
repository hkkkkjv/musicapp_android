package ru.kpfu.itis.song.api

interface SearchRepository {
    suspend fun searchSongs(query: String): SearchResult
    suspend fun getSongById(id: String): Song
}