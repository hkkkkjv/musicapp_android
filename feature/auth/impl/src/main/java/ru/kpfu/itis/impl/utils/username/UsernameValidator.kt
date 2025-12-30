package ru.kpfu.itis.impl.utils.username

import javax.inject.Inject

class UsernameValidator @Inject constructor() {

    fun validate(username: String): UsernameValidationResult {
        return when {
            username.isBlank() -> {
                UsernameValidationResult.Invalid("Username cannot be empty")
            }

            username.length < 2 -> {
                UsernameValidationResult.Invalid("Username must be at least 2 characters")
            }

            username.length > 50 -> {
                UsernameValidationResult.Invalid("Username must be at most 50 characters")
            }

            !username.matches(Regex("^[a-zA-Z0-9_-]+$")) -> {
                UsernameValidationResult.Invalid("Username can only contain letters, numbers, _, -")
            }

            else -> UsernameValidationResult.Valid(username)
        }
    }

    fun isUsernameValidForUI(username: String): Boolean {
        return username.length >= 2 && username.length <= 50 &&
                username.matches(Regex("^[a-zA-Z0-9_-]*$"))
    }

}