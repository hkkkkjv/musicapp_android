package ru.kpfu.itis.core.network.deezer.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeezerSearchResponse(
    @SerialName("data")
    val data: List<DeezerTrackDto> = emptyList()
)