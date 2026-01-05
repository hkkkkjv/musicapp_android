package ru.kpfu.itis.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.impl.domain.usecase.CheckPhoneRegisteredUseCase
import kotlin.test.assertTrue

class CheckPhoneRegisteredUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: CheckPhoneRegisteredUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = CheckPhoneRegisteredUseCase(authRepository)
    }

    @Test
    fun invokeShouldReturnTrueForRegisteredPhone() = runTest {
        val phone = "+79991234567"
        coEvery { authRepository.isPhoneRegistered(phone) } returns true

        val result = useCase(phone)

        assertTrue(result)
        coVerify { authRepository.isPhoneRegistered(phone) }
    }

    @Test
    fun invokeShouldReturnFalseForUnregisteredPhone() = runTest {
        val phone = "+79999999999"
        coEvery { authRepository.isPhoneRegistered(phone) } returns false

        val result = useCase(phone)

        assertFalse(result)
    }

    @Test
    fun invokeShouldWorkWithDifferentPhoneFormats() = runTest {
        val phones = listOf("+79991234567", "79991234567")
        for (phone in phones) {
            coEvery { authRepository.isPhoneRegistered(phone) } returns true
        }

        for (phone in phones) {
            assertTrue(useCase(phone))
        }
    }
}