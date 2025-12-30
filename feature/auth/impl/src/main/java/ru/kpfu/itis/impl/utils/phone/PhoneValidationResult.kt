package ru.kpfu.itis.impl.utils.phone

sealed class PhoneValidationResult {
    data class Valid(val phoneNumber: String) : PhoneValidationResult()
    data class Invalid(val error: String) : PhoneValidationResult()
}