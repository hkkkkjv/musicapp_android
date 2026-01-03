package ru.kpfu.itis.song.impl.presentation.details

sealed class SongDetailsEffect {
    data class LogDetailsOpened(val songId: String, val title: String) : SongDetailsEffect()
}