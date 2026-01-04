package ru.kpfu.itis.song.impl.presentation.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.core.utils.runSuspendCatching
import ru.kpfu.itis.review.api.domain.usecases.DeleteReviewUseCase
import ru.kpfu.itis.review.api.domain.usecases.GetReviewsForSongUseCase
import ru.kpfu.itis.review.api.domain.usecases.GetUserReviewUseCase
import ru.kpfu.itis.song.impl.R
import ru.kpfu.itis.song.impl.domain.GetSongDetailsUseCaseImpl
import javax.inject.Inject

class SongDetailsViewModel @Inject constructor(
    private val getSongDetailsUseCaseImpl: GetSongDetailsUseCaseImpl,
    private val getReviewsForSongUseCase: GetReviewsForSongUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val getUserReviewUseCase: GetUserReviewUseCase,
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
        loadAllReviews()
    }

    fun onEvent(event: SongDetailsEvent) {
        when (event) {
            is SongDetailsEvent.OnRetry -> load()
            is SongDetailsEvent.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }

            is SongDetailsEvent.OnDeleteReviewClick -> {
                _state.update {
                    it.copy(
                        showDeleteConfirmation = true,
                        reviewToDelete = event.reviewId
                    )
                }
            }

            is SongDetailsEvent.OnConfirmDelete -> {
                deleteReview()
            }

            is SongDetailsEvent.OnCancelDelete -> {
                _state.update { it.copy(showDeleteConfirmation = false, reviewToDelete = null) }
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

            runSuspendCatching { getSongDetailsUseCaseImpl(id) }
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
    private fun loadAllReviews() {
        val id = songId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingReviews = true) }

            try {
                val userReviewFlow = getUserReviewUseCase(id)
                val allReviewsFlow = getReviewsForSongUseCase(id)

                userReviewFlow.combine(allReviewsFlow) { userReview, allReviews ->
                    Pair(userReview, allReviews)
                }.collect { (userReview, allReviews) ->
                    Log.d("DEBUG", "userReview ID: ${userReview?.id}")
                    Log.d("DEBUG", "allReviews count: ${allReviews.size}")

                    val otherReviews = allReviews.filter { review ->
                        review.id != userReview?.id
                    }.take(5).toImmutableList()

                    _state.update {
                        it.copy(
                            userReview = userReview,
                            otherReviews = otherReviews,
                            isLoadingReviews = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "Error: ${e.message}")
                _state.update { it.copy(isLoadingReviews = false) }
            }
        }
    }

    private fun deleteReview() {
        val reviewId = _state.value.reviewToDelete ?: return
        viewModelScope.launch {
            runSuspendCatching { deleteReviewUseCase(reviewId) }
                .onSuccess {
                    _state.update { state ->
                        state.copy(
                            userReview = null,
                            showDeleteConfirmation = false,
                            reviewToDelete = null
                        )
                    }
                    _effects.emit(SongDetailsEffect.ReviewDeleted)
                }
                .onFailure { error ->
                    _state.update { it.copy(showDeleteConfirmation = false) }
                    _effects.emit(
                        SongDetailsEffect.ShowError(
                            stringProvider.getString(R.string.error_deleting_review)
                        )
                    )
                }
        }
    }
}