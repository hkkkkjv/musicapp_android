package ru.kpfu.itis.core.data.local.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kpfu.itis.core.utils.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_CACHED_SONGS)
data class CachedSong(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "artist")
    val artist: String,
    @ColumnInfo(name = "album")
    val album: String? = null,
    @ColumnInfo(name = "cover_url")
    val coverUrl: String? = null,
    @ColumnInfo(name = "preview_url")
    val previewUrl: String? = null,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "genius_url")
    val geniusUrl: String? = null,
    @ColumnInfo(name = "release_date")
    val releaseDate: String? = null,
    @ColumnInfo(name = "duration_sec")
    val durationSec: Int? = null,
    @ColumnInfo(name = "popularity")
    val popularity: Int? = null,
    @ColumnInfo(name = "lyrics")
    val lyrics: String? = null,
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)