package ru.kpfu.itis.profile.impl.presentation

sealed class ProfileEffect {
    data class NavigateToReviewDetails(val reviewId: String) : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
    data object Logout : ProfileEffect()
}