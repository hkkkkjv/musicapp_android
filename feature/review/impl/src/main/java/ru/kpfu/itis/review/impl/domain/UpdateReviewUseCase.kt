package ru.kpfu.itis.review.impl.domain

import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(review: Review) {
        val currentUser = authRepository.getCurrentUser()
            ?: throw IllegalStateException()

        val oldReview = reviewRepository.getReviewById(review.id)

        if (oldReview.userId != currentUser.id) {
            throw IllegalStateException()
        }

        reviewRepository.updateReview(review)
    }
}
