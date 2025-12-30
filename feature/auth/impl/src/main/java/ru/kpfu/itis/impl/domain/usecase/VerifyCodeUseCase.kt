package ru.kpfu.itis.impl.domain.usecase

import ru.kpfu.itis.auth.api.AuthRepository
import ru.kpfu.itis.auth.api.User
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        verificationId: String,
        smsCode: String
    ): User =
        authRepository.verifyPhoneCode(verificationId, smsCode)

}
