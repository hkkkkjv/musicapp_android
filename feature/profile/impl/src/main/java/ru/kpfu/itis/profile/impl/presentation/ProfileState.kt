package ru.kpfu.itis.profile.impl.presentation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.core.domain.models.Song

data class ProfileState(
    val isLoading: Boolean = false,

    val user: User? = null,
    val error: String? = null,
    val favoriteSongs: ImmutableList<Song> = persistentListOf(),
    val favoriteSongsCount: Int = 0,
    val isFavoriteSongsLoading: Boolean = false,
    val favoriteSongsOffset: Int = 0,
    val isFavoriteSongsExpanded: Boolean = false,

    val reviews: ImmutableList<Review> = persistentListOf(),
    val reviewsCount: Int = 0,
    val isReviewsLoading: Boolean = false,
    val reviewsOffset: Int = 0,
    val isReviewsExpanded: Boolean = false
)