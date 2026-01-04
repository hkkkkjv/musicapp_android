package ru.kpfu.itis.core.domain.models

import com.google.firebase.Timestamp

data class Review(
    val id: String = "",

    val songId: String = "",
    val songTitle: String = "",
    val songArtist: String = "",
    val songCoverUrl: String = "",
    val songSource: SongSource = SongSource.GENIUS,

    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",

    val title: String = "",
    val description: String = "",
    val pros: String = "",
    val cons: String = "",
    val rating: Float = 5f,

    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isEdited: Boolean = false
)