package ru.kpfu.itis.impl.utils.sms


sealed class SmsCodeValidationResult {
    data class Valid(val code: String) : SmsCodeValidationResult()
    data class Invalid(val error: String) : SmsCodeValidationResult()
}