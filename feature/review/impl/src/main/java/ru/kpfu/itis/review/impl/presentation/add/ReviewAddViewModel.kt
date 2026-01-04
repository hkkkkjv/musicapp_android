package ru.kpfu.itis.review.impl.presentation.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.core.utils.runSuspendCatching
import ru.kpfu.itis.review.api.domain.usecases.DeleteReviewUseCase
import ru.kpfu.itis.review.api.domain.usecases.GetUserReviewUseCase
import ru.kpfu.itis.review.impl.R
import ru.kpfu.itis.review.impl.domain.AddReviewUseCase
import ru.kpfu.itis.review.impl.domain.UpdateReviewUseCase
import ru.kpfu.itis.song.api.GetSongDetailsUseCase
import javax.inject.Inject

class ReviewAddViewModel @Inject constructor(
    private val addReviewUseCase: AddReviewUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val getSongDetailsUseCase: GetSongDetailsUseCase,
    private val getUserReviewUseCase: GetUserReviewUseCase,
    private val stringProvider: StringProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewAddState())
    val state: StateFlow<ReviewAddState> = _state

    private val _effects = MutableSharedFlow<ReviewAddEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: ReviewAddEvent) {
        when (event) {
            is ReviewAddEvent.Initialize -> initialize(event.songId)
            is ReviewAddEvent.OnTitleChanged -> onTitleChanged(event.title)
            is ReviewAddEvent.OnDescriptionChanged -> onDescriptionChanged(event.description)
            is ReviewAddEvent.OnProsChanged -> onProsChanged(event.pros)
            is ReviewAddEvent.OnConsChanged -> onConsChanged(event.cons)
            is ReviewAddEvent.OnRatingChanged -> onRatingChanged(event.rating)
            ReviewAddEvent.SubmitReviewAdd -> submitReview()
            ReviewAddEvent.ClearError -> clearError()
            ReviewAddEvent.ResetForm -> resetForm()
            is ReviewAddEvent.DeleteReviewAdd -> deleteReview(event.reviewId)
        }
    }

    private fun initialize(songId: String) {
        _state.update { it.copy(isLoading = true, songId = songId) }

        viewModelScope.launch {
            try {
                val song = getSongDetailsUseCase(songId)

                _state.update {
                    it.copy(
                        songId = song.id,
                        songTitle = song.title,
                        songArtist = song.artist,
                        songCoverUrl = song.coverUrl.toString(),
                        songSource = song.source
                    )
                }

                val userReviewFlow = getUserReviewUseCase(songId)
                val userReview = runSuspendCatching {
                    userReviewFlow.first()
                }.getOrNull()

                if (userReview != null) {
                    _state.update {
                        it.copy(
                            userReview = userReview,
                            title = userReview.title,
                            description = userReview.description,
                            pros = userReview.pros,
                            cons = userReview.cons,
                            rating = userReview.rating,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            userReview = null,
                            title = "",
                            description = "",
                            pros = "",
                            cons = "",
                            rating = 5f,
                            isLoading = false
                        )
                    }
                }
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                            ?: stringProvider.getString(R.string.error_loading_data)
                    )
                }
            }
        }
    }


    private fun onTitleChanged(title: String) {
        if (title.length <= 50) {
            _state.update { it.copy(title = title) }
        }
    }

    private fun onDescriptionChanged(description: String) {
        if (description.length <= 1000) {
            _state.update { it.copy(description = description) }
        }
    }

    private fun onProsChanged(pros: String) {
        if (pros.length <= 300) {
            _state.update { it.copy(pros = pros) }
        }
    }

    private fun onConsChanged(cons: String) {
        if (cons.length <= 300) {
            _state.update { it.copy(cons = cons) }
        }
    }

    private fun onRatingChanged(rating: Float) {
        _state.update { it.copy(rating = rating.coerceIn(1f, 5f)) }
    }

    private fun submitReview() {
        val currentState = _state.value
        if (currentState.title.isBlank()) {
            viewModelScope.launch {
                _effects.emit(ReviewAddEffect.ShowError(stringProvider.getString(R.string.error_title_empty)))
            }
            return
        }
        if (currentState.description.isBlank()) {
            viewModelScope.launch {
                _effects.emit(ReviewAddEffect.ShowError(stringProvider.getString(R.string.error_description_empty)))
            }
            return
        }

        _state.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val result = runSuspendCatching {
                val review = Review(
                    songId = currentState.songId,
                    songTitle = currentState.songTitle,
                    songArtist = currentState.songArtist,
                    songCoverUrl = currentState.songCoverUrl,
                    songSource = currentState.songSource,
                    title = currentState.title,
                    description = currentState.description,
                    pros = currentState.pros,
                    cons = currentState.cons,
                    rating = currentState.rating
                )

                val isNewReview = currentState.userReview == null

                if (isNewReview) {
                    addReviewUseCase(review)  // Возвращает String (ID)
                } else {
                    updateReviewUseCase(
                        currentState.userReview.copy(
                            title = currentState.title,
                            description = currentState.description,
                            pros = currentState.pros,
                            cons = currentState.cons,
                            rating = currentState.rating
                        )
                    )
                }
                isNewReview
            }

            result.onSuccess { isNewReview ->
                if (isNewReview) {
                    _effects.emit(ReviewAddEffect.ReviewAddAdded)
                } else {
                    _effects.emit(ReviewAddEffect.ReviewAddUpdated)
                }
                resetForm()
            }
                .onFailure { error ->
                    Log.e("ReviewViewModel", "Error submitting review: ${error.message}")
                    _effects.emit(
                        ReviewAddEffect.ShowError(
                            stringProvider.getString(R.string.unknown_error)
                        )
                    )
                }


            _state.update { it.copy(isSubmitting = false) }
        }
    }

    private fun deleteReview(reviewId: String) {
        _state.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val result = runSuspendCatching {
                deleteReviewUseCase(reviewId)
            }

            result
                .onSuccess {
                    _effects.emit(ReviewAddEffect.ReviewAddDeleted)
                    _state.update { it.copy(userReview = null) }
                    resetForm()
                }
                .onFailure { error ->
                    _effects.emit(
                        ReviewAddEffect.ShowError(
                            stringProvider.getString(R.string.unknown_error)
                        )
                    )
                }

            _state.update { it.copy(isSubmitting = false) }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun resetForm() {
        _state.update {
            it.copy(
                title = "",
                description = "",
                pros = "",
                cons = "",
                rating = 5f,
                isSubmitting = false,
                success = false
            )
        }
    }
}
