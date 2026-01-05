package ru.kpfu.itis.core.data.network.firebase.analytics

sealed class SearchEvent {
    data class Queried(val query: String) : SearchEvent()
    data class ErrorOccurred(val error: String) : SearchEvent()
    data class SongSelected(val songId: String, val title: String) : SearchEvent()
}
