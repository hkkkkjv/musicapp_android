package ru.kpfu.itis.core.data.network.genius.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusArtistDto(
    @SerialName("name")
    val name: String,
    @SerialName("image_url")
    val imagUrl: String
)