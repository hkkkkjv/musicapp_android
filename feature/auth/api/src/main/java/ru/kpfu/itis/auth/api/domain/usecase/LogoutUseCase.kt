package ru.kpfu.itis.auth.api.domain.usecase

interface LogoutUseCase {
    suspend operator fun invoke()
}