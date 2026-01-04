package ru.kpfu.itis.song.impl.presentation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: ImmutableList<Song> = persistentListOf(),
    val error: String? = null,
    val selectedSongId: String? = null,
    val isInitialized: Boolean = false,
    val selectedSource: SongSource? = SongSource.GENIUS
){
    val isEmpty: Boolean = results.isEmpty() && query.isNotEmpty() && !isLoading
    val hasError: Boolean = error != null
    val hasResults: Boolean = results.isNotEmpty()
}