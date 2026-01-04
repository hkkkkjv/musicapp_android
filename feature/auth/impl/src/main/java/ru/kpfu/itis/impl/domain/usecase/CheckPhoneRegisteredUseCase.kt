package ru.kpfu.itis.impl.domain.usecase

import ru.kpfu.itis.auth.api.data.AuthRepository
import javax.inject.Inject

class CheckPhoneRegisteredUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String): Boolean =
        authRepository.isPhoneRegistered(phone)

}