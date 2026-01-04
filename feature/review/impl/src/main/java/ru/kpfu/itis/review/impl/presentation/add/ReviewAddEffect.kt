package ru.kpfu.itis.review.impl.presentation.add

sealed class ReviewAddEffect {
    data object ReviewAddAdded : ReviewAddEffect()
    data object ReviewAddUpdated : ReviewAddEffect()
    data object ReviewAddDeleted : ReviewAddEffect()
    data object NavigateBack : ReviewAddEffect()
    data class ShowError(val message: String) : ReviewAddEffect()
    data class ShowMessage(val message: String) : ReviewAddEffect()
}