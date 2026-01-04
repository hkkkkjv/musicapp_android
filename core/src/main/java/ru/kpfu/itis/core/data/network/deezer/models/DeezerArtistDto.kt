package ru.kpfu.itis.core.data.network.deezer.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeezerArtistDto(
    @SerialName("name")
    val name: String,
    @SerialName("id")
    val id: Long? = null
)