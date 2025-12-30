package ru.kpfu.itis.impl.utils.mapping

import ru.kpfu.itis.auth.api.AuthErrorType
import ru.kpfu.itis.impl.R
import javax.inject.Inject

class ErrorMessageMapper @Inject constructor() {
    fun getStringResId(errorType: AuthErrorType): Int {
        return when (errorType) {
            AuthErrorType.INVALID_USERNAME -> R.string.error_invalid_username
            AuthErrorType.INVALID_PHONE -> R.string.error_invalid_phone
            AuthErrorType.INVALID_CODE -> R.string.error_invalid_code

            AuthErrorType.PHONE_ALREADY_REGISTERED -> R.string.error_phone_already_registered
            AuthErrorType.PHONE_NOT_REGISTERED -> R.string.error_phone_not_registered
            AuthErrorType.REGISTRATION_FAILED -> R.string.error_registration_failed

            AuthErrorType.WRONG_CODE -> R.string.error_wrong_code
            AuthErrorType.TOO_MANY_ATTEMPTS -> R.string.error_too_many_attempts
            AuthErrorType.WAIT_BEFORE_RESEND -> R.string.error_wait_before_resend

            AuthErrorType.NETWORK_ERROR -> R.string.error_network
            AuthErrorType.FIREBASE_AUTH_ERROR -> R.string.error_firebase_auth
            AuthErrorType.FIRESTORE_ERROR -> R.string.error_firestore

            AuthErrorType.USER_NOT_AUTHENTICATED -> R.string.error_user_not_authenticated
            AuthErrorType.LOGOUT_FAILED -> R.string.error_logout_failed
            AuthErrorType.UNKNOWN_ERROR -> R.string.error_unknown
        }
    }
}