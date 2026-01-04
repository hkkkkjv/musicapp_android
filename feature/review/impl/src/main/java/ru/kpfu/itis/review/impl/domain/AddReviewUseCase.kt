package ru.kpfu.itis.review.impl.domain

import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(review: Review): String {
        val currentUser = authRepository.getCurrentUser()
            ?: throw IllegalStateException()

        val hasReviewed = reviewRepository.hasUserReviewedSong(
            currentUser.id,
            review.songId
        )

        if (hasReviewed) {
            throw IllegalStateException()
        }

        return reviewRepository.addReview(
            review.copy(
                userId = currentUser.id,
                userName = currentUser.username ?: "Anonymous",
                userPhone = currentUser.phoneNumber
            )
        )
    }
}
