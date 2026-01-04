package ru.kpfu.itis.core.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object Auth

    @Serializable
    data object Profile

    @Serializable
    data object Search

    @Serializable
    data class SongDetails(val songId: String)

    @Serializable
    data class ReviewAdd(val songId: String)

    @Serializable
    data class ReviewDetails(val reviewId: String)
}
