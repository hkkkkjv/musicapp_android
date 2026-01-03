package ru.kpfu.itis.song.api

interface GetSongDetailsUseCase {
    suspend operator fun invoke(id: String): Song
}