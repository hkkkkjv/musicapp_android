package ru.kpfu.itis.song.impl.presentation

import ru.kpfu.itis.song.api.Song
import ru.kpfu.itis.song.api.SongSource

sealed class SearchEvent {
    data class OnQueryChanged(val value: String) : SearchEvent()
    data class OnSearchClicked(val query: String) : SearchEvent()
    data class OnSongClicked(val song: Song) : SearchEvent()
    object OnClearResults : SearchEvent()
    object OnClearError : SearchEvent()
    object OnRetry : SearchEvent()
    data class OnSourceFilterChanged(val source: SongSource?) : SearchEvent()
}