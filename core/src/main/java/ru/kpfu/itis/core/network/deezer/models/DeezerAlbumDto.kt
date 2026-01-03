package ru.kpfu.itis.core.network.deezer.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeezerAlbumDto(
    @SerialName("title")
    val title: String? = null,
    @SerialName("cover_big")
    val cover: String? = null,
    @SerialName("id")
    val id: Long? = null
)