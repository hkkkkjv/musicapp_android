package ru.kpfu.itis.core.navigation

import kotlinx.serialization.Serializable

sealed class NavKey : androidx.navigation3.runtime.NavKey{
    @Serializable
    data object Auth : NavKey()

    @Serializable
    data object Search : NavKey()

    @Serializable
    data object Profile : NavKey()

    @Serializable
    data class SongDetails(val songId: String) : NavKey()

    @Serializable
    data class ReviewAdd(val songId: String) : NavKey()

    @Serializable
    data class ReviewDetails(val reviewId: String) : NavKey()
}