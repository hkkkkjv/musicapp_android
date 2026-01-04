package ru.kpfu.itis.profile.api.data

import kotlinx.coroutines.flow.Flow
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.core.domain.models.Review

interface ProfileRepository {

    suspend fun getUserProfile(): User

    suspend fun getUserReviews(limit: Int = 10, offset: Int = 0): List<Review>

    suspend fun getUserReviewsCount(): Int

    fun watchUserProfile(): Flow<User>
}
