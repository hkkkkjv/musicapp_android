package ru.kpfu.itis.song.impl

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource
import ru.kpfu.itis.song.api.SearchRepository
import ru.kpfu.itis.song.impl.domain.GetSongDetailsUseCaseImpl
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GetSongDetailsUseCaseTest {

    private lateinit var searchRepository: SearchRepository
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var useCase: GetSongDetailsUseCaseImpl

    @Before
    fun setUp() {
        searchRepository = mockk()
        analytics = mockk(relaxed = true)
        useCase = GetSongDetailsUseCaseImpl(searchRepository, analytics)
    }

    @Test
    fun invokeShouldReturnSongWithLyricsForGenius() = runTest {
        val songId = "genius:123"
        val lyrics = "When I find myself in times of trouble, Mother Mary comes to me..."
        val expectedSong = Song(
            id = songId,
            title = "Let It Be",
            artist = "The Beatles",
            album = null,
            coverUrl = "https://example.com/let-it-be.jpg",
            previewUrl = null,
            source = SongSource.GENIUS,
            geniusUrl = "https://genius.com/The-beatles-let-it-be-lyrics",
            releaseDate = "1970-05-08",
            durationSec = 243,
            popularity = 95,
            lyrics = lyrics
        )

        coEvery { searchRepository.getSongById(songId) } returns expectedSong

        val result = useCase(songId)

        assertEquals(expectedSong, result)
        assertEquals("Let It Be", result.title)
        assertNotNull(result.lyrics)
        assertEquals(lyrics, result.lyrics)
        coVerify { searchRepository.getSongById(songId) }
    }

    @Test
    fun invokeShouldReturnSongWithoutLyricsForDeezer() = runTest {
        val songId = "deezer:456"
        val expectedSong = Song(
            id = songId,
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

        coEvery { searchRepository.getSongById(songId) } returns expectedSong

        val result = useCase(songId)

        assertEquals(expectedSong, result)
        assertEquals(null, result.lyrics)
    }

    @Test
    fun invokeShouldPropagateExceptionFromRepository() = runTest {
        val songId = "invalid:id"
        val exception = IllegalArgumentException("Invalid song ID")

        coEvery { searchRepository.getSongById(songId) } throws exception

        assertFailsWith<IllegalArgumentException> {
            useCase(songId)
        }
    }
}