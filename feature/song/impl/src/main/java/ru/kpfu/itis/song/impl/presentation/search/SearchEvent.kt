package ru.kpfu.itis.song.impl.presentation.search

import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource

sealed class SearchEvent {
    data class OnQueryChanged(val value: String) : SearchEvent()
    data class OnSearchClicked(val query: String) : SearchEvent()
    data class OnSongClicked(val song: Song) : SearchEvent()
    object OnClearResults : SearchEvent()
    object OnClearError : SearchEvent()
    object OnRetry : SearchEvent()
    data class OnSourceFilterChanged(val source: SongSource?) : SearchEvent()
}