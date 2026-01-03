package ru.kpfu.itis.song.impl.presentation.details

sealed class SongDetailsNavigation {
    data class OpenLyrics(val url: String) : SongDetailsNavigation()
}