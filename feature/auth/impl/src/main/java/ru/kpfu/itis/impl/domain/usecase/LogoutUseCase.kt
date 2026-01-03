package ru.kpfu.itis.impl.domain.usecase

import ru.kpfu.itis.auth.api.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}