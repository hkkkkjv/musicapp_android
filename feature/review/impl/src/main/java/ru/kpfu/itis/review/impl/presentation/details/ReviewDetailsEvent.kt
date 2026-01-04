package ru.kpfu.itis.review.impl.presentation.details

sealed interface ReviewDetailsEvent {
    data class OnRetry(val reviewId: String) : ReviewDetailsEvent
    object OnDismissError : ReviewDetailsEvent
    object OnDeleteClick : ReviewDetailsEvent
    object OnConfirmDelete : ReviewDetailsEvent
    object OnCancelDelete : ReviewDetailsEvent
}
