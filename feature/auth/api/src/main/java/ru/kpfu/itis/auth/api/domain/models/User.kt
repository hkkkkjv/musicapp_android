package ru.kpfu.itis.auth.api.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val phoneNumber: String,
    val username: String?,
    val photoUrl: String?,
    val createdAt: Long
)