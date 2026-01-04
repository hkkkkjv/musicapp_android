package ru.kpfu.itis.profile.impl.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.profile.api.data.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProfileRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw Exception("currentUserId error")

    override suspend fun getUserProfile(): User =
        firestore.collection("users")
            .document(currentUserId)
            .get()
            .await()
            .toObject(User::class.java)
            ?: throw Exception("getUserProfile error")


    override suspend fun getUserReviews(limit: Int, offset: Int): List<Review> {
        val query = firestore.collection("reviews")
            .whereEqualTo("userId", currentUserId)
            //.orderBy("createdAt", Query.Direction.DESCENDING)
            .limit((limit + offset).toLong())
            .get()
            .await()

        val allDocs = query.documents

        val startIndex = offset.coerceAtMost(allDocs.size)
        val endIndex = (offset + limit).coerceAtMost(allDocs.size)

        return allDocs.slice(startIndex until endIndex)
            .mapNotNull { it.toObject(Review::class.java)?.copy(id = it.id) }
    }

    override suspend fun getUserReviewsCount(): Int {
        return firestore.collection("reviews")
            .whereEqualTo("userId", currentUserId)
            .get()
            .await()
            .size()
    }

    override fun watchUserProfile(): Flow<User> = flow {
        try {
            firestore.collection("users")
                .document(currentUserId)
                .get()
                .await()
                .toObject(User::class.java)?.let {
                    emit(it)
                }
        } catch (e: Exception) {
        }
    }
}