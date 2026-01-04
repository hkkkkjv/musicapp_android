package ru.kpfu.itis.review.impl.domain

import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import javax.inject.Inject

class GetReviewByIdUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(reviewId: String): Review {
        require(reviewId.isNotBlank())
        return reviewRepository.getReviewById(reviewId)
    }
}