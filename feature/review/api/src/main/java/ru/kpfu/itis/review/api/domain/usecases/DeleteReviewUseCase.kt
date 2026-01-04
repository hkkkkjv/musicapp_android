package ru.kpfu.itis.review.api.domain.usecases

interface DeleteReviewUseCase {
    suspend operator fun invoke(reviewId: String)
}