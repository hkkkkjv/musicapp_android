package ru.kpfu.itis.song.impl.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.song.impl.R
import ru.kpfu.itis.song.impl.domain.SearchSongsUseCase
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchSongsUseCase: SearchSongsUseCase,
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SearchEffect>(replay = 0)
    val effects: SharedFlow<SearchEffect> = _effects

    private val _navigation = MutableSharedFlow<SearchNavigation>(replay = 0)
    val navigation: SharedFlow<SearchNavigation> = _navigation

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChanged -> reduce(event)
            is SearchEvent.OnSearchClicked -> reduce(event)
            is SearchEvent.OnSongClicked -> reduce(event)
            is SearchEvent.OnClearResults -> reduce(event)
            is SearchEvent.OnClearError -> reduce(event)
            is SearchEvent.OnRetry -> reduce(event)
            is SearchEvent.OnSourceFilterChanged -> reduce(event)
        }
    }

    private fun reduce(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChanged -> {
                _state.update { it.copy(query = event.value, error = null) }
            }

            is SearchEvent.OnSearchClicked -> {
                search(event.query)
            }

            is SearchEvent.OnSongClicked -> {
                _state.update { it.copy(selectedSongId = event.song.id) }

                viewModelScope.launch {
                    _effects.emit(
                        SearchEffect.LogSongClick(event.song.id, event.song.title)
                    )
                }

                viewModelScope.launch {
                    _navigation.emit(
                        SearchNavigation.OpenSongDetails(event.song.id)
                    )
                }
            }

            is SearchEvent.OnClearResults -> {
                clearResults()
            }

            is SearchEvent.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is SearchEvent.OnRetry -> {
                search(state.value.query)
            }

            is SearchEvent.OnSourceFilterChanged -> {
                _state.update { it.copy(selectedSource = event.source) }
            }
        }
    }

    private fun search(query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            viewModelScope.launch {
                _effects.emit(SearchEffect.ShowToast(stringProvider.getString(R.string.enter_song_or_artist_name)))
            }
            _state.update { it.copy(error = stringProvider.getString(R.string.enter_song_or_artist_name)) }
            return
        }

        viewModelScope.launch {
            _effects.emit(SearchEffect.LogSearch(trimmedQuery))
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            runCatching { searchSongsUseCase(trimmedQuery) }
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            results = result.songs.toImmutableList(),
                            query = trimmedQuery,
                            error = if (result.songs.isEmpty()) {
                                stringProvider.getString(R.string.no_songs_found)
                            } else {
                                null
                            },
                            isInitialized = true
                        )
                    }

                    if (result.songs.isEmpty()) {
                        viewModelScope.launch {
                            _effects.emit(SearchEffect.ShowToast(stringProvider.getString(R.string.no_songs_found)))
                        }
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = stringProvider.getString(R.string.search_failed),
                            isInitialized = true
                        )
                    }

                    viewModelScope.launch {
                        _effects.emit(
                            SearchEffect.ShowToast(stringProvider.getString(R.string.search_failed))
                        )
                    }
                }
        }
    }

    private fun clearResults() {
        _state.update {
            it.copy(
                results = persistentListOf(),
                error = null,
                selectedSource = null
            )
        }
    }
}