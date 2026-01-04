package ru.kpfu.itis.review.impl.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import ru.kpfu.itis.review.api.domain.usecases.GetUserReviewUseCase
import javax.inject.Inject

class GetUserReviewUseCaseImpl @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) : GetUserReviewUseCase {
    override operator fun invoke(songId: String): Flow<Review?> {
        val currentUser = authRepository.getCurrentUser() ?: return flowOf(null)
        return reviewRepository.getUserReviewForSong(
            userId = currentUser.id,
            songId = songId
        )
    }
}