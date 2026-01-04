package ru.kpfu.itis.review.impl.presentation.add

import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.core.domain.models.SongSource

data class ReviewAddState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val songId: String = "",
    val songTitle: String = "",
    val songArtist: String = "",
    val songCoverUrl: String = "",
    val songSource: SongSource = SongSource.GENIUS,

    val title: String = "",
    val description: String = "",
    val pros: String = "",
    val cons: String = "",
    val rating: Float = 5f,

    val userReview: Review? = null,

    val error: String? = null,
    val success: Boolean = false
)

