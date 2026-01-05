package ru.kpfu.itis.core.data.network.firebase.analytics

sealed class ScreenEvent(val screenName: String, val screenClass: String) {
    object SearchScreen : ScreenEvent("search", "SearchScreen")
    object SongDetailsScreen : ScreenEvent("song_details", "SongDetailsScreen")
    object ProfileScreen : ScreenEvent("profile", "ProfileScreen")
    object ReviewAddScreen : ScreenEvent("review_add", "ReviewAddScreen")
    object ReviewDetailsScreen : ScreenEvent("review_details", "ReviewDetailsScreen")
    object AuthScreen : ScreenEvent("auth", "AuthScreen")
}
