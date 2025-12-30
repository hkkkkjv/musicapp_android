package ru.kpfu.itis.impl.utils.sms

import javax.inject.Inject

class SmsCodeValidator @Inject constructor()  {
    fun validate(code: String): SmsCodeValidationResult {
        return when {
            code.isBlank() -> {
                SmsCodeValidationResult.Invalid("SMS code cannot be empty")
            }
            code.length != 6 -> {
                SmsCodeValidationResult.Invalid("SMS code must be exactly 6 digits")
            }
            !code.all { it.isDigit() } -> {
                SmsCodeValidationResult.Invalid("SMS code must contain only digits")
            }
            else -> SmsCodeValidationResult.Valid(code)
        }
    }
}