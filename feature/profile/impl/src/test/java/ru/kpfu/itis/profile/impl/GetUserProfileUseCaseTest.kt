package ru.kpfu.itis.profile.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.profile.api.data.ProfileRepository
import kotlin.test.assertEquals
import com.google.firebase.Timestamp
import ru.kpfu.itis.profile.impl.domain.GetUserProfileUseCase

class GetUserProfileUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var useCase: GetUserProfileUseCase

    @Before
    fun setUp() {
        profileRepository = mockk()
        useCase = GetUserProfileUseCase(profileRepository)
    }

    @Test
    fun invokeShouldReturnUserProfile() = runTest {
        val mockUser = User(
            id = "user:1",
            username = "john_doe",
            phoneNumber = "+79991234567",
            photoUrl = "https://example.com/photo.jpg",
            createdAt = Timestamp.now()
        )

        coEvery { profileRepository.getUserProfile() } returns mockUser

        val result = useCase()

        assertEquals("user:1", result.id)
        assertEquals("john_doe", result.username)
        assertEquals("+79991234567", result.phoneNumber)
        coVerify { profileRepository.getUserProfile() }
    }

    @Test
    fun invokeShouldPropagateExceptionFromRepository() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { profileRepository.getUserProfile() } throws exception

        try {
            useCase()
            assert(false) { "Should have thrown exception" }
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }
}