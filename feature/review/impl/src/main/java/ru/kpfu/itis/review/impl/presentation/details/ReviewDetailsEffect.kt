package ru.kpfu.itis.review.impl.presentation.details

sealed interface ReviewDetailsEffect {
    object ReviewDeleted : ReviewDetailsEffect
    data class ShowError(val message: String) : ReviewDetailsEffect
}
