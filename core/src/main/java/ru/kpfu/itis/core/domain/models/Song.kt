package ru.kpfu.itis.core.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("artist")
    val artist: String,
    @SerialName("album")
    val album: String?,
    @SerialName("cover_url")
    val coverUrl: String?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("source")
    val source: SongSource,
    @SerialName("genius_url")
    val geniusUrl: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("duration_sec")
    val durationSec: Int?,
    @SerialName("popularity")
    val popularity: Int?,
    val lyrics: String? = null
)