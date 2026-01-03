package ru.kpfu.itis.auth.api

import android.app.Activity

sealed class AuthEvent {

    object GoToLogin : AuthEvent()
    object GoToRegister : AuthEvent()
    object BackFromName : AuthEvent()
    object BackFromPhone : AuthEvent()
    object BackFromCode : AuthEvent()

    data class OnNameChanged(val name: String) : AuthEvent()
    data class OnPhoneChanged(val phone: String) : AuthEvent()
    data class OnCodeChanged(val code: String) : AuthEvent()

    data class OnNameSubmitted(val name: String) : AuthEvent()
    data class OnPhoneSendCode(val activity: Activity, val phone: String) : AuthEvent()
    data class OnCodeSubmitted(val code: String) : AuthEvent()
    data class OnCodeResend(val activity: Activity, val phone: String) : AuthEvent()

    data object OnErrorDismiss : AuthEvent()
    data object OnLogout : AuthEvent()
}