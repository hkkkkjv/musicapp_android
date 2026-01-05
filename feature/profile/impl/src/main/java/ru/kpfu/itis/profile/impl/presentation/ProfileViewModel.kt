package ru.kpfu.itis.profile.impl.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kpfu.itis.auth.api.domain.usecase.LogoutUseCase
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.core.utils.runSuspendCatching
import ru.kpfu.itis.profile.impl.R
import ru.kpfu.itis.profile.impl.domain.GetUserProfileUseCase
import ru.kpfu.itis.profile.impl.domain.GetUserReviewsUseCase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserReviewsUseCase: GetUserReviewsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    private val _effects = MutableSharedFlow<ProfileEffect>()
    val effects = _effects.asSharedFlow()

    private companion object {
        const val PAGE_SIZE = 3
    }

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            ProfileEvent.LoadReviews -> loadReviews()
            ProfileEvent.LoadMoreReviews -> loadMoreReviews()
            ProfileEvent.ToggleReviewsExpanded -> toggleReviewsExpanded()
            is ProfileEvent.OnReviewClick -> onReviewClick(event.reviewId)

            is ProfileEvent.ClearError -> clearError(event.errorType)
            ProfileEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = runSuspendCatching {
                getUserProfileUseCase()
            }

            result.onSuccess { user ->
                Log.i("ProfileViewModel", user.toString())
                _state.update { it.copy(user = user, isLoading = false) }
                loadReviewsCount()
            }
                .onFailure { error ->
                    Log.i(
                        "ProfileViewModel",
                        error.message ?: stringProvider.getString(R.string.error_load_profile)
                    )
                    _state.update {
                        it.copy(
                            error = error.message
                                ?: stringProvider.getString(R.string.error_load_profile),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadReviewsCount() {
        viewModelScope.launch {
            val count = runSuspendCatching {
                getUserReviewsUseCase.getCount()
            }.getOrNull() ?: 0

            _state.update { it.copy(reviewsCount = count) }

            if (count > 0 && _state.value.reviews.isEmpty()) {
                loadReviews()
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isReviewsLoading = true,
                    reviewsOffset = 0
                )
            }

            val result = runSuspendCatching {
                getUserReviewsUseCase(
                    limit = PAGE_SIZE,
                    offset = _state.value.reviewsOffset
                )
            }

            result
                .onSuccess { reviews ->
                    Log.i("loadReviews", reviews.size.toString())
                    reviews.forEach { review ->
                        Log.d(
                            "loadReviews", "Review: id='${review.id}', " +
                                    "songId='${review.songId}', " +
                                    "rating=${review.rating}, " +
                                    "title='${review.title}', " +
                                    "text='${review.description}'"
                        )
                    }
                    _state.update {
                        it.copy(
                            reviews = reviews.toImmutableList(),
                            isReviewsLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        Log.i("loadReviews", stringProvider.getString(R.string.unknown_error))
                        it.copy(
                            error = error.message
                                ?: stringProvider.getString(R.string.unknown_error),
                            isReviewsLoading = false
                        )
                    }
                }
        }
    }

    private fun loadMoreReviews() {
        if (_state.value.isReviewsLoading) return
        if (_state.value.reviews.size >= _state.value.reviewsCount) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isReviewsLoading = true,
                    reviewsOffset = it.reviewsOffset + PAGE_SIZE
                )
            }

            val result = runSuspendCatching {
                getUserReviewsUseCase(
                    limit = PAGE_SIZE,
                    offset = _state.value.reviewsOffset
                )
            }

            result
                .onSuccess { newReviews ->
                    _state.update {
                        it.copy(
                            reviews = (it.reviews + newReviews).toImmutableList(),
                            isReviewsLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            error = error.message
                                ?: stringProvider.getString(R.string.unknown_error),
                            isReviewsLoading = false
                        )
                    }
                }
        }
    }

    private fun onReviewClick(reviewId: String) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Review clicked: $reviewId")
            emitEffect(ProfileEffect.NavigateToReviewDetails(reviewId))
        }
    }

    private fun toggleReviewsExpanded() {
        val newExpanded = !_state.value.isReviewsExpanded
        _state.update { it.copy(isReviewsExpanded = newExpanded) }
    }

    private fun clearError(errorType: ProfileEvent.ErrorType?) {
        _state.update { it.copy(error = null) }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = runSuspendCatching {
                logoutUseCase()
            }

            result
                .onSuccess {
                    emitEffect(ProfileEffect.Logout)
                    Log.i("ProfileViewModel", "Logout")
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    emitEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_logout)))
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: stringProvider.getString(R.string.error_logout)
                        )
                    }
                }
        }
    }

    private fun emitEffect(effect: ProfileEffect) {
        viewModelScope.launch {
            Log.d("PROFILE-VIEW-MODEL", "Emitting effect: $effect")
            _effects.emit(effect)
        }
    }
}
