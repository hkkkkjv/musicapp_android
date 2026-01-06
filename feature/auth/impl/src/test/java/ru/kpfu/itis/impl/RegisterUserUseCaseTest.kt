package ru.kpfu.itis.impl

import com.google.firebase.Timestamp
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.impl.domain.usecase.RegisterUserUseCase
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RegisterUserUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: RegisterUserUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = RegisterUserUseCase(authRepository)
    }

    @Test
    fun invokeShouldRegisterUserSuccessfully() = runTest {
        val username = "olga_p"
        val phone = "+79991234567"
        val expectedUser = User(
            id = "user:123",
            username = username,
            phoneNumber = phone,
            photoUrl = null,
            createdAt = Timestamp.now()
        )

        coEvery { authRepository.registerUser(username, phone) } returns expectedUser

        val result = useCase(username, phone)

        assertEquals(expectedUser, result)
        assertEquals("olga_p", result.username)
        assertEquals("+79991234567", result.phoneNumber)
        coVerify { authRepository.registerUser(username, phone) }
    }

    @Test
    fun invokeShouldThrowExceptionForInvalidUsername() = runTest {
        val username = ""
        val phone = "+79991234567"
        val exception = IllegalArgumentException("Username cannot be empty")

        coEvery { authRepository.registerUser(username, phone) } throws exception

        assertFailsWith<IllegalArgumentException> {
            useCase(username, phone)
        }
    }

    @Test
    fun invokeShouldThrowExceptionForInvalidPhone() = runTest {
        val username = "olga"
        val phone = ""
        val exception = IllegalArgumentException("Phone cannot be empty")

        coEvery { authRepository.registerUser(username, phone) } throws exception

        assertFailsWith<IllegalArgumentException> {
            useCase(username, phone)
        }
    }
}