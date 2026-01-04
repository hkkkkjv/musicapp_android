package ru.kpfu.itis.review.api.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.kpfu.itis.core.domain.models.Review

interface GetReviewsForSongUseCase {
    operator fun invoke(songId: String): Flow<List<Review>>
}