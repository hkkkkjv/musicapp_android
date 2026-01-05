package ru.kpfu.itis.review.impl

import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import ru.kpfu.itis.review.impl.domain.GetUserReviewUseCaseImpl
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetUserReviewUseCaseTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: GetUserReviewUseCaseImpl

    @Before
    fun setUp() {
        reviewRepository = mockk()
        authRepository = mockk()
        useCase = GetUserReviewUseCaseImpl(reviewRepository, authRepository)
    }

    @Test
    fun invokeShouldReturnUsersReviewForSong() = runTest {
        val songId = "genius:123"
        val userId = "user:1"
        val userReview = Review(
            id = "review:1",
            songId = songId,
            userId = userId,
            userName = "John",
            userPhone = "+79991234567",
            rating = 5f,
            title = "My review",
            description = "Perfect song",
            pros = "Everything",
            cons = "Nothing",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )
        val mockUser = mockk<User> {
            every { id } returns userId
        }
        val reviewFlow: Flow<Review?> = flowOf(userReview)

        every { authRepository.getCurrentUser() } returns mockUser
        every { reviewRepository.getUserReviewForSong(userId, songId) } returns reviewFlow

        val result = useCase(songId).toList()

        assertEquals(1, result.size)
        assertEquals(userReview, result[0])
        verify { reviewRepository.getUserReviewForSong(userId, songId) }
    }

    @Test
    fun invokeShouldReturnNullWhenUserNotLoggedIn() = runTest {
        val songId = "genius:123"
        every { authRepository.getCurrentUser() } returns null

        val result = useCase(songId).toList()

        assertEquals(1, result.size)
        assertNull(result[0])
    }

    @Test
    fun invokeShouldReturnNullWhenUserHasNoReviewForSong() = runTest {
        // Arrange
        val songId = "genius:123"
        val userId = "user:1"
        val mockUser = mockk<User> {
            every { id } returns userId
        }
        val reviewFlow: Flow<Review?> = flowOf(null)

        every { authRepository.getCurrentUser() } returns mockUser
        every { reviewRepository.getUserReviewForSong(userId, songId) } returns reviewFlow

        // Act
        val result = useCase(songId).toList()

        // Assert
        assertEquals(1, result.size)
        assertNull(result[0])
    }
}