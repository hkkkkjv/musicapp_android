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
import ru.kpfu.itis.review.impl.domain.AddReviewUseCase
import kotlin.test.assertFailsWith

class AddReviewUseCaseTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: AddReviewUseCase

    @Before
    fun setUp() {
        reviewRepository = mockk()
        authRepository = mockk()
        useCase = AddReviewUseCase(reviewRepository, authRepository)
    }

    @Test
    fun invokeShouldAddReviewSuccessfully() = runTest {
        val userId = "user:1"
        val songId = "genius:123"
        val mockUser = User(
            id = userId,
            username = "olga_p",
            phoneNumber = "+79991234567",
            photoUrl = null,
            createdAt = Timestamp.now()
        )
        val review = Review(
            id = "",
            songId = songId,
            userId = userId,
            userName = "olga_p",
            userPhone = "+79991234567",
            rating = 5f,
            title = "Great song!",
            description = "Really awesome",
            pros = "Everything",
            cons = "Nothing",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )
        val reviewId = "review:1"

        coEvery { authRepository.getCurrentUser() } returns mockUser
        coEvery { reviewRepository.hasUserReviewedSong(userId, songId) } returns false
        coEvery { reviewRepository.addReview(any()) } returns reviewId

        val result = useCase(review)

        assert(result == reviewId)
        coVerify { reviewRepository.addReview(any()) }
    }

    @Test
    fun invokeShouldFailWhenUserNotLoggedIn() = runTest {
        val review = Review(
            id = "",
            songId = "genius:123",
            userId = "",
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
    fun invokeShouldFailWhenUserAlreadyReviewed() = runTest {
        val userId = "user:1"
        val songId = "genius:123"
        val mockUser = User(
            id = userId,
            username = "olga_p",
            phoneNumber = "+79991234567",
            photoUrl = null,
            createdAt = Timestamp.now()
        )
        val review = Review(
            id = "",
            songId = songId,
            userId = userId,
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

        coEvery { authRepository.getCurrentUser() } returns mockUser
        coEvery { reviewRepository.hasUserReviewedSong(userId, songId) } returns true

        assertFailsWith<IllegalStateException> {
            useCase(review)
        }
    }
}