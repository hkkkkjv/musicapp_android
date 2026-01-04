package ru.kpfu.itis.impl.data.exceptions

import ru.kpfu.itis.auth.api.presentation.AuthErrorType

class AuthRepositoryException(
    val errorType: AuthErrorType,
    cause: Throwable? = null
) : Exception(cause?.message ?: errorType.name, cause)