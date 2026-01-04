package ru.kpfu.itis.song.impl.presentation.details

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.core.domain.models.Song

data class SongDetailsState(
    val isLoading: Boolean = false,
    val song: Song? = null,
    val error: String? = null,
    val isInitialized: Boolean = false,

    val userReview: Review? = null,
    val otherReviews: ImmutableList<Review> = persistentListOf(),
    val isLoadingReviews: Boolean = false,
    val reviewToDelete: String? = null,
    val showDeleteConfirmation: Boolean = false,
)