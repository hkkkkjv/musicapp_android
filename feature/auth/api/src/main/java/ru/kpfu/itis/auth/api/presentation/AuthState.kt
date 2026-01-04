package ru.kpfu.itis.auth.api.presentation

import ru.kpfu.itis.auth.api.domain.models.User

sealed class AuthState {

    object Loading : AuthState()

    object LoginOrRegister : AuthState()
    data class EnteringUsername(
        val name: String = "",
        val isLoading: Boolean = false
    ) : AuthState()

    data class EnteringPhone(
        val phone: String = "",
        val isRegistration: Boolean = false,
        val userName: String = "",
        val isLoading: Boolean = false
    ) : AuthState()

    data class EnteringCode(
        val verificationId: String,
        val code: String = "",
        val isRegistration: Boolean = false,
        val userName: String = "",
        val phone: String = "",
        val attemptsLeft: Int = 3,
        val resendCountdown: Int = 30,
        val isLoading: Boolean = false
    ) : AuthState()

    data class Authenticated(val user: User) : AuthState()

    data class Error(val message: String) : AuthState()
}
