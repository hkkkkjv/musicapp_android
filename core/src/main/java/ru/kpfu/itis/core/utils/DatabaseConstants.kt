package ru.kpfu.itis.core.utils

object DatabaseConstants {
    const val DATABASE_NAME = "app_database"
    const val DATABASE_VERSION = 1
    const val TABLE_CACHED_SONGS = "cached_songs"
    const val TABLE_SEARCH_CACHE = "search_cache"
    const val CACHE_DURATION_MS = 24 * 60 * 60 * 1000L
}