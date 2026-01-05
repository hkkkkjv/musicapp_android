package ru.kpfu.itis.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.impl.domain.usecase.LogoutUseCaseImpl

class LogoutUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var useCase: LogoutUseCaseImpl

    @Before
    fun setUp() {
        authRepository = mockk()
        useCase = LogoutUseCaseImpl(authRepository)
    }

    @Test
    fun invokeShouldCallRepositoryLogout() = runTest {
        coEvery { authRepository.logout() } returns Unit

        useCase()

        coVerify { authRepository.logout() }
    }

    @Test
    fun invokeShouldCompleteSuccessfully() = runTest {
        coEvery { authRepository.logout() } returns Unit

        val result = runCatching { useCase() }

        assert(result.isSuccess)
    }
}