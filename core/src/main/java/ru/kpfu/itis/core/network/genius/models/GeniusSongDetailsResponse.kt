package ru.kpfu.itis.core.network.genius.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusSongDetailsResponse(
    @SerialName("response")
    val response: GeniusSongDetailsWrapper
)