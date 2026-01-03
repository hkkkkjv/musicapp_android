package ru.kpfu.itis.musicapp.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object Auth

    @Serializable
    data object Home

    @Serializable
    data object Search

    @Serializable
    data class SongDetails(val songId: String)
}
