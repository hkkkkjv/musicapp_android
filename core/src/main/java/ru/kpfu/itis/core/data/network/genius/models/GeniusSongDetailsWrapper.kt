package ru.kpfu.itis.core.data.network.genius.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusSongDetailsWrapper(
    @SerialName("song")
    val song: GeniusSongDto
)