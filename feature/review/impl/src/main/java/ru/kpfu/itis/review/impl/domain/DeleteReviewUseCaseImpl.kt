package ru.kpfu.itis.review.impl.domain

import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.review.api.data.ReviewRepository
import ru.kpfu.itis.review.api.domain.usecases.DeleteReviewUseCase
import javax.inject.Inject

class DeleteReviewUseCaseImpl @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) : DeleteReviewUseCase {
    override suspend operator fun invoke(reviewId: String) {
        val currentUser = authRepository.getCurrentUser()
            ?: throw IllegalStateException()

        val review = reviewRepository.getReviewById(reviewId)

        if (review.userId != currentUser.id) {
            throw IllegalStateException()
        }

        reviewRepository.deleteReview(reviewId)
    }
}
