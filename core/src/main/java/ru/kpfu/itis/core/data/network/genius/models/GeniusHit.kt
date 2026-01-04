package ru.kpfu.itis.core.data.network.genius.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusHit(
    @SerialName("result")
    val result: GeniusSongDto
)