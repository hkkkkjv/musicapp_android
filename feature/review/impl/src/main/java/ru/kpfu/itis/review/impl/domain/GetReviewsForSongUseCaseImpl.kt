package ru.kpfu.itis.review.impl.domain

import kotlinx.coroutines.flow.Flow
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import ru.kpfu.itis.review.api.domain.usecases.GetReviewsForSongUseCase
import javax.inject.Inject

class GetReviewsForSongUseCaseImpl @Inject constructor(
    private val reviewRepository: ReviewRepository
) : GetReviewsForSongUseCase {
    override operator fun invoke(songId: String): Flow<List<Review>> =
        reviewRepository.getReviewsForSong(songId)
}
