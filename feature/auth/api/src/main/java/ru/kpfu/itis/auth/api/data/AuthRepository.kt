package ru.kpfu.itis.auth.api.data

import ru.kpfu.itis.auth.api.domain.models.User

interface AuthRepository {
    suspend fun isPhoneRegistered(phone: String): Boolean
    suspend fun registerUser(
        username: String,
        phone: String
    ): User

    suspend fun verifyPhoneCode(
        verificationId: String,
        smsCode: String
    ): User

    suspend fun loginUser(phone: String): User

    fun isLoggedIn(): Boolean

    fun getCurrentUser(): User?

    suspend fun logout()
}
