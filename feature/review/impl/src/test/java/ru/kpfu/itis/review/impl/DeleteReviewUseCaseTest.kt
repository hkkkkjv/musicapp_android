package ru.kpfu.itis.review.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import kotlin.test.assertFailsWith
import com.google.firebase.Timestamp
import io.mockk.every
import ru.kpfu.itis.review.impl.domain.DeleteReviewUseCaseImpl

class DeleteReviewUseCaseTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: DeleteReviewUseCaseImpl

    @Before
    fun setUp() {
        reviewRepository = mockk()
        authRepository = mockk()
        useCase = DeleteReviewUseCaseImpl(reviewRepository, authRepository)
    }

    @Test
    fun invokeShouldDeleteUsersOwnReview() = runTest {
        val reviewId = "review:1"
        val userId = "user:1"
        val mockUser = mockk<User> {
            every { id } returns userId
        }
        val review = Review(
            id = reviewId,
            userId = userId,
            songId = "genius:123",
            userName = "John",
            userPhone = "+79991234567",
            rating = 5f,
            title = "My review",
            description = "Good",
            pros = "Nice",
            cons = "Long",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )

        coEvery { authRepository.getCurrentUser() } returns mockUser
        coEvery { reviewRepository.getReviewById(reviewId) } returns review
        coEvery { reviewRepository.deleteReview(reviewId) } returns Unit

        useCase(reviewId)

        coVerify { reviewRepository.deleteReview(reviewId) }
    }

    @Test
    fun invokeShouldFailWhenTryingToDeleteSomeoneElsesReview() = runTest {
        val reviewId = "review:1"
        val userId = "user:1"
        val ownerId = "user:2"
        val mockUser = mockk<User> {
            every { id } returns userId
        }
        val review = Review(
            id = reviewId,
            userId = ownerId,
            songId = "genius:123",
            userName = "Jane",
            userPhone = "+79998765432",
            rating = 4f,
            title = "Her review",
            description = "Not bad",
            pros = "Melody",
            cons = "Length",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )

        coEvery { authRepository.getCurrentUser() } returns mockUser
        coEvery { reviewRepository.getReviewById(reviewId) } returns review

        assertFailsWith<IllegalStateException> {
            useCase(reviewId)
        }
        coVerify(exactly = 0) { reviewRepository.deleteReview(any()) }
    }

    @Test
    fun invokeShouldFailWhenUserNotLoggedIn() = runTest {
        val reviewId = "review:1"
        coEvery { authRepository.getCurrentUser() } returns null

        assertFailsWith<IllegalStateException> {
            useCase(reviewId)
        }
    }
}