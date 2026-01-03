package ru.kpfu.itis.song.impl.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.song.impl.R
import ru.kpfu.itis.song.impl.domain.GetSongDetailsUseCaseImpl
import javax.inject.Inject

class SongDetailsViewModel @Inject constructor(
    private val getSongDetailsUseCaseImpl: GetSongDetailsUseCaseImpl,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _state = MutableStateFlow(SongDetailsState(isLoading = true))
    val state: StateFlow<SongDetailsState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SongDetailsEffect>(replay = 0)
    val effects: SharedFlow<SongDetailsEffect> = _effects

    private val _navigation = MutableSharedFlow<SongDetailsNavigation>(replay = 0)
    val navigation: SharedFlow<SongDetailsNavigation> = _navigation

    private var songId: String? = null

    fun initialize(songId: String) {
        if (this.songId != null) return
        this.songId = songId
        load()
    }

    fun onEvent(event: SongDetailsEvent) {
        when (event) {
            is SongDetailsEvent.OnRetry -> load()
            is SongDetailsEvent.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun load() {
        val id = songId
        if (id == null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = stringProvider.getString(R.string.song_id_is_missing),
                    isInitialized = true
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching { getSongDetailsUseCaseImpl(id) }
                .onSuccess { song ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            song = song,
                            isInitialized = true
                        )
                    }

                    viewModelScope.launch {
                        _effects.emit(
                            SongDetailsEffect.LogDetailsOpened(song.id, song.title)
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = stringProvider.getString(R.string.failed_to_load_song),
                            isInitialized = true
                        )
                    }
                }
        }
    }
}