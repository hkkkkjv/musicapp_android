package ru.kpfu.itis.review.impl.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    override suspend fun addReview(review: Review): String {
        require(review.title.isNotBlank() && review.title.length <= 50)
        require(review.description.isNotBlank() && review.description.length <= 1000)
        require(review.rating in 1f..5f)

        val reviewWithTimestamps = review.copy(
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )

        val documentRef = firestore.collection("reviews").add(review).await()
        return documentRef.id
    }

    override fun getReviewsForSong(songId: String): Flow<List<Review>> =
        callbackFlow {
            val listener = firestore.collection("reviews")
                .whereEqualTo("songId", songId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val reviews = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            Log.i(
                                "getReviewsForSong",
                                doc.toObject(Review::class.java)?.copy(id = doc.id).toString()
                            )
                            doc.toObject(Review::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    trySend(reviews).isSuccess
                }

            awaitClose { listener.remove() }
        }

    override fun getReviewsForUser(userId: String): Flow<List<Review>> =
        callbackFlow {
            val listener = firestore.collection("reviews")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val reviews = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Review::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    trySend(reviews).isSuccess
                }

            awaitClose { listener.remove() }
        }

    override suspend fun getReviewById(reviewId: String): Review {
        val doc = firestore.collection("reviews").document(reviewId).get().await()

        return doc.toObject(Review::class.java)?.copy(id = doc.id)
            ?: throw NoSuchElementException()
    }

    override suspend fun updateReview(review: Review): Unit {
        require(review.id.isNotBlank())

        val updateData = mapOf(
            "title" to review.title,
            "description" to review.description,
            "pros" to review.pros,
            "cons" to review.cons,
            "rating" to review.rating,
            "updatedAt" to Timestamp.now(),
            "isEdited" to true
        )

        firestore.collection("reviews")
            .document(review.id)
            .update(updateData)
            .await()
    }

    override suspend fun deleteReview(reviewId: String): Unit {
        firestore.collection("reviews").document(reviewId).delete().await()
    }

    override suspend fun hasUserReviewedSong(
        userId: String,
        songId: String
    ): Boolean {
        val snapshot = firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .whereEqualTo("songId", songId)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    override fun getUserReviewForSong(
        userId: String,
        songId: String
    ): Flow<Review?> = callbackFlow {
        val listener = firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .whereEqualTo("songId", songId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val review = snapshot?.documents?.firstOrNull()?.let {
                    try {
                        Log.i(
                            "getUserReviewForSong",
                            it.toObject(Review::class.java)?.copy(id = it.id).toString()
                        )
                        it.toObject(Review::class.java)?.copy(id = it.id)
                    } catch (e: Exception) {
                        null
                    }
                }

                trySend(review).isSuccess
            }

        awaitClose { listener.remove() }
    }
}
