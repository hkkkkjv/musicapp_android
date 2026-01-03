package ru.kpfu.itis.impl.utils.username

sealed class UsernameValidationResult {
    data class Valid(val username: String) : UsernameValidationResult()
    data class Invalid(val error: String) : UsernameValidationResult()
}
