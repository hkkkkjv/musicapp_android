package ru.kpfu.itis.review.impl.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kpfu.itis.review.impl.R
import ru.kpfu.itis.review.impl.presentation.components.ReviewForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewAddScreen(
    viewModel: ReviewAddViewModel,
    songId: String,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(ReviewAddEvent.Initialize(songId = songId))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ReviewAddEffect.ReviewAddAdded -> onBack()

                ReviewAddEffect.ReviewAddUpdated -> onBack()

                ReviewAddEffect.ReviewAddDeleted -> onBack()

                is ReviewAddEffect.ShowError -> {
                }

                is ReviewAddEffect.ShowMessage -> {
                }

                ReviewAddEffect.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.songTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (state.error != null) {
                ErrorBanner(
                    error = state.error!!,
                    onDismiss = { viewModel.onEvent(ReviewAddEvent.ClearError) }
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    ReviewForm(
                        title = state.title,
                        onTitleChanged = { viewModel.onEvent(ReviewAddEvent.OnTitleChanged(it)) },
                        description = state.description,
                        onDescriptionChanged = {
                            viewModel.onEvent(
                                ReviewAddEvent.OnDescriptionChanged(
                                    it
                                )
                            )
                        },
                        pros = state.pros,
                        onProsChanged = { viewModel.onEvent(ReviewAddEvent.OnProsChanged(it)) },
                        cons = state.cons,
                        onConsChanged = { viewModel.onEvent(ReviewAddEvent.OnConsChanged(it)) },
                        rating = state.rating,
                        onRatingChanged = { viewModel.onEvent(ReviewAddEvent.OnRatingChanged(it)) },
                        isSubmitting = state.isSubmitting,
                        onSubmit = { viewModel.onEvent(ReviewAddEvent.SubmitReviewAdd) },
                        isEditMode = state.userReview != null,
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorBanner(
    error: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(12.dp)
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
