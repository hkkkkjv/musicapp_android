package ru.kpfu.itis.impl.utils.phone

import javax.inject.Inject

class PhoneValidator @Inject constructor() {
    fun validate(phoneNumber: String): PhoneValidationResult {
        return when {
            phoneNumber.isBlank() -> {
                PhoneValidationResult.Invalid("Phone number cannot be empty")
            }

            !phoneNumber.startsWith("+") -> {
                PhoneValidationResult.Invalid("Phone number must start with +")
            }

            phoneNumber.length < 10 -> {
                PhoneValidationResult.Invalid("Phone number is too short")
            }

            phoneNumber.length > 15 -> {
                PhoneValidationResult.Invalid("Phone number is too long")
            }

            !phoneNumber.drop(1).all { it.isDigit() } -> {
                PhoneValidationResult.Invalid("Phone number must contain only digits after +")
            }

            else -> PhoneValidationResult.Valid(phoneNumber)
        }
    }

    fun isPhoneValidForUI(phoneNumber: String): Boolean {
        val digitsOnly = phoneNumber.replace(Regex("\\D"), "")
        return digitsOnly.length == 10
    }
}