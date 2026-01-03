package ru.kpfu.itis.song.impl.domain

import com.google.firebase.analytics.FirebaseAnalytics
import ru.kpfu.itis.song.api.GetSongDetailsUseCase
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.api.Song
import javax.inject.Inject

class GetSongDetailsUseCaseImpl @Inject constructor(
    private val repository: SearchRepository,
    private val analytics: FirebaseAnalytics
) : GetSongDetailsUseCase {
    override suspend operator fun invoke(id: String): Song =
        repository.getSongById(id)

}
