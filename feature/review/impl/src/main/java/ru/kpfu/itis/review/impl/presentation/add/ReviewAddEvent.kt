package ru.kpfu.itis.review.impl.presentation.add

sealed class ReviewAddEvent {
    data class Initialize(val songId: String) : ReviewAddEvent()
    data class OnTitleChanged(val title: String) : ReviewAddEvent()
    data class OnDescriptionChanged(val description: String) : ReviewAddEvent()
    data class OnProsChanged(val pros: String) : ReviewAddEvent()
    data class OnConsChanged(val cons: String) : ReviewAddEvent()
    data class OnRatingChanged(val rating: Float) : ReviewAddEvent()
    data object SubmitReviewAdd : ReviewAddEvent()
    data object ClearError : ReviewAddEvent()
    data object ResetForm : ReviewAddEvent()
    data class DeleteReviewAdd(val reviewId: String) : ReviewAddEvent()
}
