package ru.kpfu.itis.song.impl.presentation

sealed class SearchEffect {
    data class LogSearch(val query: String) : SearchEffect()
    data class LogSongClick(val songId: String, val title: String) : SearchEffect()
    data class ShowToast(val message: String) : SearchEffect()
}