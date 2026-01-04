package ru.kpfu.itis.review.impl.domain

import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.review.api.data.ReviewRepository
import javax.inject.Inject

class CheckUserReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(songId: String): Boolean {
        val currentUser = authRepository.getCurrentUser() ?: return false
        return reviewRepository.hasUserReviewedSong(
            userId = currentUser.id,
            songId = songId
        )
    }
}