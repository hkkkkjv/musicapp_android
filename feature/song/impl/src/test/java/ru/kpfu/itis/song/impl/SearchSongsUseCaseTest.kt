package ru.kpfu.itis.song.impl

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.api.SearchResult
import ru.kpfu.itis.song.impl.domain.SearchSongsUseCase

class SearchSongsUseCaseTest {

    private lateinit var searchRepository: SearchRepository
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var useCase: SearchSongsUseCase

    @Before
    fun setUp() {
        searchRepository = mockk()
        analytics = mockk(relaxed = true)
        useCase = SearchSongsUseCase(searchRepository, analytics)
    }

    @Test
    fun invokeShouldTrimQueryAndCallRepository() = runTest {
        val queryWithWhitespace = "  The Beatles  "
        val trimmedQuery = "The Beatles"
        val mockSongs = listOf(
            Song(
                id = "genius:123",
                title = "Let It Be",
                artist = "The Beatles",
                source = SongSource.GENIUS,
                album = null,
                coverUrl = null,
                previewUrl = null,
                geniusUrl = null,
                releaseDate = null,
                durationSec = null,
                popularity = null,
                lyrics = null
            )
        )
        val expectedResult = SearchResult(query = trimmedQuery, songs = mockSongs)

        coEvery { searchRepository.searchSongs(trimmedQuery) } returns expectedResult

        val result = useCase(queryWithWhitespace)

        assertEquals(expectedResult, result)
        coVerify { searchRepository.searchSongs(trimmedQuery) }
    }

    @Test
    fun invokeShouldReturnSearchResultWithMultipleSongs() = runTest {
        val query = "The Beatles"
        val songs = listOf(
            Song(
                id = "genius:123",
                title = "Let It Be",
                artist = "The Beatles",
                album = null,
                coverUrl = "https://example.com/let-it-be.jpg",
                previewUrl = null,
                source = SongSource.GENIUS,
                geniusUrl = "https://genius.com/...",
                releaseDate = "1970-05-08",
                durationSec = 243,
                popularity = 95,
                lyrics = null
            ),
            Song(
                id = "deezer:456",
                title = "Abbey Road",
                artist = "The Beatles",
                album = "Abbey Road",
                coverUrl = "https://example.com/abbey-road.jpg",
                previewUrl = "https://example.com/preview.mp3",
                source = SongSource.DEEZER,
                geniusUrl = null,
                releaseDate = "1969-09-26",
                durationSec = 268,
                popularity = 98,
                lyrics = null
            )
        )
        val expectedResult = SearchResult(query = query, songs = songs)

        coEvery { searchRepository.searchSongs(query) } returns expectedResult

        val result = useCase(query)

        assertEquals(2, result.songs.size)
        assertEquals("Let It Be", result.songs[0].title)
        assertEquals("Abbey Road", result.songs[1].title)
    }

    @Test
    fun invokeShouldReturnEmptyListWhenNoSongsFound() = runTest {
        val query = "NonExistentBand"
        val expectedResult = SearchResult(query = query, songs = emptyList())

        coEvery { searchRepository.searchSongs(query) } returns expectedResult

        val result = useCase(query)

        assertTrue(result.songs.isEmpty())
    }
}