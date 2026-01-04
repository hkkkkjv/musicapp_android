package ru.kpfu.itis.core.data.network.genius.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusSongDto(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("primary_artist")
    val primaryArtist: GeniusArtistDto,
    @SerialName("song_art_image_url")
    val songArtImageUrl: String? = null,
    @SerialName("url")
    val url: String
)