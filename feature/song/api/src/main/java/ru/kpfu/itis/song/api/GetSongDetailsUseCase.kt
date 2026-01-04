package ru.kpfu.itis.song.api

import ru.kpfu.itis.core.domain.models.Song

interface GetSongDetailsUseCase {
    suspend operator fun invoke(id: String): Song
}