package ru.kpfu.itis.review.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import com.google.firebase.Timestamp
import ru.kpfu.itis.review.impl.domain.GetReviewByIdUseCase

class GetReviewByIdUseCaseTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var useCase: GetReviewByIdUseCase

    @Before
    fun setUp() {
        reviewRepository = mockk()
        useCase = GetReviewByIdUseCase(reviewRepository)
    }

    @Test
    fun invokeShouldReturnReviewById() = runTest {
        val reviewId = "review:1"
        val expectedReview = Review(
            id = reviewId,
            songId = "genius:123",
            userId = "user:1",
            userName = "olga_p",
            userPhone = "+79991234567",
            rating = 5f,
            title = "Great!",
            description = "Amazing song",
            pros = "Everything",
            cons = "Nothing",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            isEdited = false
        )

        coEvery { reviewRepository.getReviewById(reviewId) } returns expectedReview

        val result = useCase(reviewId)

        assertEquals(expectedReview, result)
        assertEquals("Great!", result.title)
        coVerify { reviewRepository.getReviewById(reviewId) }
    }

    @Test
    fun invokeShouldThrowExceptionForBlankReviewId() = runTest {
        val reviewId = ""

        assertFailsWith<IllegalArgumentException> {
            useCase(reviewId)
        }
    }

    @Test
    fun invokeShouldThrowExceptionWhenReviewNotFound() = runTest {
        val reviewId = "nonexistent:id"
        val exception = NoSuchElementException("Review not found")

        coEvery { reviewRepository.getReviewById(reviewId) } throws exception

        assertFailsWith<NoSuchElementException> {
            useCase(reviewId)
        }
    }
}