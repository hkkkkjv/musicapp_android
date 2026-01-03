package ru.kpfu.itis.core.network.genius.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusSearchResponse(
    @SerialName("response")
    val response: GeniusResponseWrapper
)