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
import ru.kpfu.itis.impl.domain.usecase.VerifyCodeUseCase
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VerifyCodeUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: VerifyCodeUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = VerifyCodeUseCase(authRepository)
    }

    @Test
    fun invokeShouldVerifyCodeSuccessfully() = runTest {
        val verificationId = "verification:123"
        val smsCode = "123456"
        val expectedUser = User(
            id = "user:456",
            username = "olga_p",
            phoneNumber = "+79991234567",
            photoUrl = null,
            createdAt = Timestamp.now()
        )

        coEvery { authRepository.verifyPhoneCode(verificationId, smsCode) } returns expectedUser

        val result = useCase(verificationId, smsCode)

        assertEquals(expectedUser, result)
        assertEquals("user:456", result.id)
        coVerify { authRepository.verifyPhoneCode(verificationId, smsCode) }
    }

    @Test
    fun invokeShouldThrowExceptionForInvalidCode() = runTest {
        val verificationId = "verification:123"
        val smsCode = "000000"
        val exception = IllegalArgumentException("Invalid SMS code")

        coEvery { authRepository.verifyPhoneCode(verificationId, smsCode) } throws exception

        assertFailsWith<IllegalArgumentException> {
            useCase(verificationId, smsCode)
        }
    }

    @Test
    fun invokeShouldThrowExceptionForExpiredVerificationId() = runTest {
        val verificationId = "expired:id"
        val smsCode = "123456"
        val exception = IllegalStateException("Verification ID expired")

        coEvery { authRepository.verifyPhoneCode(verificationId, smsCode) } throws exception

        assertFailsWith<IllegalStateException> {
            useCase(verificationId, smsCode)
        }
    }
}