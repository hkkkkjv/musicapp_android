package ru.kpfu.itis.review.impl.presentation.details

import ru.kpfu.itis.core.domain.models.Review

data class ReviewDetailsState(
    val isLoading: Boolean = false,
    val review: Review? = null,
    val isOwner: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val isDeleting: Boolean = false,
    val isInitialized: Boolean = false
)
