package ru.kpfu.itis.song.impl.presentation.details

import ru.kpfu.itis.song.api.Song

data class SongDetailsState(
    val isLoading: Boolean = false,
    val song: Song? = null,
    val error: String? = null,
    val isInitialized: Boolean = false
)