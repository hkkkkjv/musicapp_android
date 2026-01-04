package ru.kpfu.itis.impl.domain.usecase

import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.usecase.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) :LogoutUseCase{
    override suspend operator fun invoke() {
        authRepository.logout()
    }
}