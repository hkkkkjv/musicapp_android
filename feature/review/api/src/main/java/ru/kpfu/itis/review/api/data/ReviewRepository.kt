package ru.kpfu.itis.review.api.data

import kotlinx.coroutines.flow.Flow
import ru.kpfu.itis.core.domain.models.Review

interface ReviewRepository {

    suspend fun addReview(review: Review): String

    fun getReviewsForSong(songId: String): Flow<List<Review>>
    fun getReviewsForUser(userId: String): Flow<List<Review>>
    suspend fun getReviewById(reviewId: String): Review

    suspend fun updateReview(review: Review)

    suspend fun deleteReview(reviewId: String)

    suspend fun hasUserReviewedSong(userId: String, songId: String): Boolean

    fun getUserReviewForSong(userId: String, songId: String): Flow<Review?>
}