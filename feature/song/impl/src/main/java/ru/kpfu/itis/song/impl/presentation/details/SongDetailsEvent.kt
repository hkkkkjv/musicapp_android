package ru.kpfu.itis.song.impl.presentation.details

sealed class SongDetailsEvent {
    object OnRetry : SongDetailsEvent()
    object OnDismissError : SongDetailsEvent()
    data class OnDeleteReviewClick(val reviewId: String) : SongDetailsEvent()
    data object OnConfirmDelete : SongDetailsEvent()
    data object OnCancelDelete : SongDetailsEvent()
}
