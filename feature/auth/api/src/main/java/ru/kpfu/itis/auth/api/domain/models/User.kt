package ru.kpfu.itis.auth.api.domain.models

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val phoneNumber: String = "",
    val username: String? = null,
    val photoUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)