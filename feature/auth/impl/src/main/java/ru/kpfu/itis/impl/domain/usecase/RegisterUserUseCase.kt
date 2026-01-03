package ru.kpfu.itis.impl.domain.usecase

import ru.kpfu.itis.auth.api.AuthRepository
import ru.kpfu.itis.auth.api.User
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, phone: String): User =
        authRepository.registerUser(username, phone)

}