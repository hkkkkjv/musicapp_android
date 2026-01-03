package ru.kpfu.itis.song.impl.presentation.details

sealed class SongDetailsEvent {
    object OnRetry : SongDetailsEvent()
    object OnDismissError : SongDetailsEvent()
}
