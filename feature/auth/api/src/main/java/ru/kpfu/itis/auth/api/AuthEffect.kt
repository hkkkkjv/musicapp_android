package ru.kpfu.itis.auth.api

sealed class AuthEffect {
    data class CheckPhoneRegistration(
        val phone: String
    ) : AuthEffect()

    data class SendSmsCode(
        val phone: String,
        val isRegistration: Boolean = false,
        val userName: String = ""
    ) : AuthEffect()


    data class VerifyPhoneCode(
        val verificationId: String,
        val code: String,
        val isRegistration: Boolean = false,
        val userName: String = "",
        val phone: String = ""
    ) : AuthEffect()

    data class ResendSmsCode(
        val phone: String,
        val userName: String = "",
        val isRegistration: Boolean = false
    ) : AuthEffect()

    object PerformLogout : AuthEffect()

    data class ShowError(val message: String) : AuthEffect()
    data class ShowSuccess(val message: String) : AuthEffect()

    data class NavigateToHome(val user: User) : AuthEffect()
}