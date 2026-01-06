package ru.kpfu.itis.impl

import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.impl.domain.usecase.GetCurrentUserIdUseCaseImpl
import kotlin.test.assertEquals

class GetCurrentUserIdUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: GetCurrentUserIdUseCaseImpl

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = GetCurrentUserIdUseCaseImpl(authRepository)
    }

    @Test
    fun invokeShouldReturnUserId() {
        val userId = "user:12345"
        val mockUser = User(
            id = userId,
            username = "olga",
            phoneNumber = "+79991234567",
            photoUrl = null,
            createdAt = Timestamp.now()
        )

        every { authRepository.getCurrentUser() } returns mockUser

        val result = useCase()

        assertEquals(userId, result)
        verify { authRepository.getCurrentUser() }
    }

    @Test
    fun invokeShouldReturnEmptyStringWhenUserNotLoggedIn() {
        every { authRepository.getCurrentUser() } returns null

        val result = useCase()

        assertEquals("", result)
    }
}