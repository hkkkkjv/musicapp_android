package ru.kpfu.itis.auth.api.domain.usecase

interface GetCurrentUserIdUseCase {
    operator fun invoke(): String
}