package ru.kpfu.itis.review.impl

import com.google.firebase.Timestamp
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
import ru.kpfu.itis.review.impl.domain.UpdateReviewUseCase
import kotlin.test.assertFailsWith

class UpdateReviewUseCaseTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: UpdateReviewUseCase

    @Before
    fun setUp() {
        reviewRepository = mockk()
        authRepository = mockk()
        useCase = UpdateReviewUseCase(reviewRepository, authRepository)
    }

    @Test
    fun invokeShouldUpdateReviewSuccessfully() = runTest {
        val userId = "user:1"
        val reviewId = "review:1"
        val mockUser = User(
            id = userId,
            username = "olga_p",
            phoneNumber = "+79991234567",
            photoUrl = null,
            createdAt = Timestamp.now()
        )
        val oldReview = Review(
            id = reviewId,
            songId = "genius:123",
            userId = userId,
            userName = "olga_p",
            userPhone = "+79991234567",
            rating = 3f,
            title = "Good",
            description = "Good",
            pros = "Nice",
            cons = "Short",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )
        val updatedReview = oldReview.copy(
            rating = 5f,
            title = "Great!",
            isEdited = true
        )

        coEvery { authRepository.getCurrentUser() } returns mockUser
        coEvery { reviewRepository.getReviewById(reviewId) } returns oldReview
        coEvery { reviewRepository.updateReview(any()) } returns Unit

        useCase(updatedReview)

        coVerify { reviewRepository.updateReview(any()) }
    }

    @Test
    fun invokeShouldFailWhenUserNotLoggedIn() = runTest {
        val review = Review(
            id = "review:1",
            songId = "genius:123",
            userId = "user:1",
            userName = "olga_p",
            userPhone = "+79991234567",
            rating = 5f,
            title = "Test",
            description = "Test",
            pros = "Test",
            cons = "Test",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )

        coEvery { authRepository.getCurrentUser() } returns null

        assertFailsWith<IllegalStateException> {
            useCase(review)
        }
    }

    @Test
    fun invokeShouldFailWhenUserTriesToUpdateOthersReview() = runTest {
        val userId = "user:1"
        val ownerId = "user:2"
        val reviewId = "review:1"
        val mockUser = User(
            id = userId,
            username = "olga_p",
            phoneNumber = "+79991234567",
            photoUrl = null,
            createdAt = Timestamp.now()
        )
        val oldReview = Review(
            id = reviewId,
            songId = "genius:123",
            userId = ownerId,
            userName = "olga_p",
            userPhone = "+79998765432",
            rating = 4f,
            title = "Good",
            description = "Good",
            pros = "Nice",
            cons = "Long",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )
        val updatedReview = oldReview.copy(rating = 1f)

        coEvery { authRepository.getCurrentUser() } returns mockUser
        coEvery { reviewRepository.getReviewById(reviewId) } returns oldReview

        assertFailsWith<IllegalStateException> {
            useCase(updatedReview)
        }
    }
}