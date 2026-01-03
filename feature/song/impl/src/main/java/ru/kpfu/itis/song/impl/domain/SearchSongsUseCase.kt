package ru.kpfu.itis.song.impl.domain

import com.google.firebase.analytics.FirebaseAnalytics
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.api.SearchResult
import javax.inject.Inject

class SearchSongsUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val analytics: FirebaseAnalytics
) {
    suspend operator fun invoke(query: String): SearchResult =
        repository.searchSongs(query.trim())

}
