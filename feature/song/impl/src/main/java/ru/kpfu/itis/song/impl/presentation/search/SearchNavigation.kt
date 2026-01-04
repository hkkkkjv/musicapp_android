package ru.kpfu.itis.song.impl.presentation.search

sealed class SearchNavigation {
    data class OpenSongDetails(val songId: String) : SearchNavigation()
}