package ru.kpfu.itis.review.impl.presentation.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kpfu.itis.auth.api.domain.usecase.GetCurrentUserIdUseCase
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.core.data.network.firebase.analytics.ScreenEvent
import ru.kpfu.itis.core.utils.StringProvider
import ru.kpfu.itis.core.utils.runSuspendCatching
import ru.kpfu.itis.review.api.domain.usecases.DeleteReviewUseCase
import ru.kpfu.itis.review.impl.R
import ru.kpfu.itis.review.impl.domain.GetReviewByIdUseCase
import javax.inject.Inject

class ReviewDetailsViewModel @Inject constructor(
    private val getReviewByIdUseCase: GetReviewByIdUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val stringProvider: StringProvider,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewDetailsState(isLoading = true))
    val state: StateFlow<ReviewDetailsState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ReviewDetailsEffect>(replay = 0)
    val effects: SharedFlow<ReviewDetailsEffect> = _effects

    private var reviewId: String? = null

    fun initialize(reviewId: String) {
        if (this.reviewId != null) return
        this.reviewId = reviewId
        analyticsManager.logScreenOpened(
            screenName = ScreenEvent.ReviewDetailsScreen.screenName,
            screenClass = ScreenEvent.ReviewDetailsScreen.screenClass
        )
        loadReview()
    }

    fun onEvent(event: ReviewDetailsEvent) {
        when (event) {
            is ReviewDetailsEvent.OnRetry -> loadReview()
            ReviewDetailsEvent.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }

            ReviewDetailsEvent.OnDeleteClick -> {
                if (!_state.value.isOwner) {
                    Log.w("ReviewDetails", "User is not owner, cannot delete review")
                    return
                }
                _state.update { it.copy(showDeleteConfirmation = true) }
            }

            ReviewDetailsEvent.OnConfirmDelete -> {
                deleteReview()
            }

            ReviewDetailsEvent.OnCancelDelete -> {
                _state.update { it.copy(showDeleteConfirmation = false) }
            }

            ReviewDetailsEvent.OnRefresh -> loadReview(isRefresh = true)
        }
    }

    private fun loadReview(isRefresh: Boolean = false) {
        val id = reviewId ?: return
        analyticsManager.startPerformanceTrace("load_review_details")
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = if (isRefresh) it.isLoading else true,
                    isRefreshing = isRefresh,
                    error = null
                )
            }
            runSuspendCatching { getReviewByIdUseCase(id) }
                .onSuccess { review ->
                    Log.d("ReviewDetails", "Review loaded: ${review.id}")

                    val currentUserId = getCurrentUserIdUseCase()
                    val isOwner = review.userId == currentUserId
                    analyticsManager.stopPerformanceTrace("load_review_details")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            review = review,
                            isRefreshing = false,
                            isOwner = isOwner,
                            isInitialized = true
                        )
                    }
                }

                .onFailure { error ->
                    analyticsManager.stopPerformanceTrace("load_review_details")
                    analyticsManager.logLoadingError("review_details", error.message ?: "Unknown")
                    Log.e("ReviewDetails", "Error loading review: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = stringProvider.getString(R.string.failed_to_load_review),
                            isInitialized = true
                        )
                    }
                }
        }
    }

    private fun deleteReview() {
        val id = reviewId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            runSuspendCatching { deleteReviewUseCase(id) }
                .onSuccess {
                    analyticsManager.logReviewDeleted(id)
                    Log.d("ReviewDetails", "Review deleted: $id")
                    _state.update { it.copy(isDeleting = false) }
                    _effects.emit(ReviewDetailsEffect.ReviewDeleted)
                }
                .onFailure { error ->
                    analyticsManager.logNonFatalException(error as Exception, "delete_review")
                    Log.e("ReviewDetails", "Error deleting review: ${error.message}")
                    _state.update { it.copy(isDeleting = false, showDeleteConfirmation = false) }
                    _effects.emit(
                        ReviewDetailsEffect.ShowError(
                            stringProvider.getString(R.string.error_deleting_review)
                        )
                    )
                }
        }
    }
}
