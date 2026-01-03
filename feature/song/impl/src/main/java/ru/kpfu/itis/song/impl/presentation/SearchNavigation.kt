package ru.kpfu.itis.song.impl.presentation

sealed class SearchNavigation {
    data class OpenSongDetails(val songId: String) : SearchNavigation()
}