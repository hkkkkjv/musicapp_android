package ru.kpfu.itis.core.data.local.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kpfu.itis.core.utils.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_SEARCH_CACHE)
data class SearchCache(
    @PrimaryKey
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "song_ids")
    val songIds: String,
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
