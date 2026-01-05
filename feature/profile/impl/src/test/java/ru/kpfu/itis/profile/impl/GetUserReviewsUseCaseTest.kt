package ru.kpfu.itis.profile.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.profile.api.data.ProfileRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.google.firebase.Timestamp
import ru.kpfu.itis.profile.impl.domain.GetUserReviewsUseCase

class GetUserReviewsUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var useCase: GetUserReviewsUseCase

    @Before
    fun setUp() {
        profileRepository = mockk()
        useCase = GetUserReviewsUseCase(profileRepository)
    }

    @Test
    fun invokeShouldReturnUserReviews() = runTest {
        val limit = 10
        val offset = 0
        val reviews = listOf(
            Review(
                id = "review:1",
                songId = "genius:123",
                userId = "user:1",
                userName = "olga_p",
                userPhone = "+79991234567",
                rating = 5f,
                title = "Great!",
                description = "Amazing",
                pros = "Everything",
                cons = "Nothing",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isEdited = false
            ),
            Review(
                id = "review:2",
                songId = "deezer:456",
                userId = "user:1",
                userName = "olga_p",
                userPhone = "+79991234567",
                rating = 4f,
                title = "Good",
                description = "Nice",
                pros = "Melody",
                cons = "Length",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isEdited = false
            )
        )

        coEvery { profileRepository.getUserReviews(limit, offset) } returns reviews

        val result = useCase(limit, offset)

        assertEquals(2, result.size)
        assertEquals("Great!", result[0].title)
        assertEquals("Good", result[1].title)
        coVerify { profileRepository.getUserReviews(limit, offset) }
    }

    @Test
    fun invokeShouldReturnEmptyListWhenNoReviews() = runTest {
        coEvery { profileRepository.getUserReviews(any(), any()) } returns emptyList()

        val result = useCase()

        assertTrue(result.isEmpty())
    }

    @Test
    fun invokeShouldReturnReviewsWithDefaultLimitAndOffset() = runTest {
        val reviews = listOf(
            Review(
                id = "review:1",
                songId = "genius:123",
                userId = "user:1",
                userName = "olga_p",
                userPhone = "+79991234567",
                rating = 5f,
                title = "Great!",
                description = "Amazing",
                pros = "Everything",
                cons = "Nothing",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isEdited = false
            )
        )

        coEvery { profileRepository.getUserReviews(10, 0) } returns reviews

        val result = useCase()

        assertEquals(1, result.size)
        coVerify { profileRepository.getUserReviews(10, 0) }
    }
}