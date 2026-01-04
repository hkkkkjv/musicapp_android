package ru.kpfu.itis.review.api.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.kpfu.itis.core.domain.models.Review

interface GetUserReviewUseCase {
    operator fun invoke(songId: String): Flow<Review?>
}