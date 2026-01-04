package ru.kpfu.itis.profile.impl.presentation

sealed class ProfileEvent {
    data object LoadProfile : ProfileEvent()

    data object LoadReviews : ProfileEvent()
    data object LoadMoreReviews : ProfileEvent()

    data object ToggleReviewsExpanded : ProfileEvent()

    data class OnReviewClick(val reviewId: String) : ProfileEvent()

    data object Logout : ProfileEvent()
    data class ClearError(val errorType: ErrorType? = null) : ProfileEvent()

    enum class ErrorType {
        PROFILE, REVIEWS, LOGOUT
    }
}