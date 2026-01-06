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
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.api.data.ReviewRepository
import ru.kpfu.itis.review.impl.domain.GetReviewsForSongUseCaseImpl
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetReviewsForSongUseCaseTest {

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var useCase: GetReviewsForSongUseCaseImpl

    @Before
    fun setUp() {
        reviewRepository = mockk()
        useCase = GetReviewsForSongUseCaseImpl(reviewRepository)
    }

    @Test
    fun invokeShouldReturnReviewsForSong() = runTest {
        val songId = "genius:123"
        val reviews = listOf(
            Review(
                id = "review:1",
                songId = songId,
                userId = "user:1",
                userName = "John",
                userPhone = "+79991234567",
                rating = 5f,
                title = "Great song!",
                description = "Really amazing",
                pros = "Everything",
                cons = "Nothing",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isEdited = false
            ),
            Review(
                id = "review:2",
                songId = songId,
                userId = "user:2",
                userName = "Jane",
                userPhone = "+79998765432",
                rating = 4f,
                title = "Good but not perfect",
                description = "Nice song",
                pros = "Melody",
                cons = "Length",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isEdited = false
            )
        )
        val reviewFlow: Flow<List<Review>> = flowOf(reviews)

        every { reviewRepository.getReviewsForSong(songId) } returns reviewFlow

        val result = useCase(songId).toList()

        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals("Great song!", result[0][0].title)
        verify { reviewRepository.getReviewsForSong(songId) }
    }

    @Test
    fun invokeShouldReturnEmptyListWhenNoReviews() = runTest {
        val songId = "nonexistent:id"
        val emptyFlow: Flow<List<Review>> = flowOf(emptyList())

        every { reviewRepository.getReviewsForSong(songId) } returns emptyFlow

        val result = useCase(songId).toList()

        assertEquals(1, result.size)
        assertTrue(result[0].isEmpty())
    }
}