package ru.kpfu.itis.profile.impl.domain

import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.profile.api.data.ProfileRepository
import javax.inject.Inject

class GetUserReviewsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(limit: Int = 10, offset: Int = 0): List<Review> =
        profileRepository.getUserReviews(limit, offset)

    suspend fun getCount(): Int = profileRepository.getUserReviewsCount()
}
