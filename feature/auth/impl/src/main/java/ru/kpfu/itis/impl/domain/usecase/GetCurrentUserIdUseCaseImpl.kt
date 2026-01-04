package ru.kpfu.itis.impl.domain.usecase

import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.usecase.GetCurrentUserIdUseCase
import javax.inject.Inject

class GetCurrentUserIdUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : GetCurrentUserIdUseCase {
    override operator fun invoke(): String =
        authRepository.getCurrentUser()?.id ?: ""

}
