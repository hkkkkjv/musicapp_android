package ru.kpfu.itis.song.impl.presentation.details


import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.delay
import ru.kpfu.itis.core.data.media.GlobalMusicPlayer
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.core.presentation.components.DeleteConfirmDialog
import ru.kpfu.itis.song.impl.R
import ru.kpfu.itis.song.impl.presentation.details.components.SongDetailsContent

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailsScreen(
    viewModel: SongDetailsViewModel,
    songId: String,
    onBack: () -> Unit,
    onReviewAddClick: (String) -> Unit,
    onReviewDetailsClick: (String) -> Unit = {},
    analyticsManager: AnalyticsManager,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val globalPlayer = remember {
        GlobalMusicPlayer.getInstance(context)
    }

    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var isLoadingPreview by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val coverSize by animateDpAsState(
        targetValue = if (scrollState.value < 50) 340.dp else 200.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cover_animation"
    )
    LaunchedEffect(songId) {
        viewModel.initialize(songId)
    }

    DisposableEffect(Unit) {
        val listener = object : GlobalMusicPlayer.PlayerListener {
            override fun onPlaybackStateChanged(isPlayingNow: Boolean, durationMs: Long) {
                isPlaying = isPlayingNow
                if (durationMs > 0) duration = durationMs.toFloat()

                if (isPlayingNow && state.song != null) {
                    Log.d("SongDetails", "Showing notification for ${state.song!!.title}")
//                    notificationManager.showNotification(
//                        songTitle = state.song!!.title,
//                        artistName = state.song!!.artist,
//                        isPlaying = true
//                    )
                }
            }

            override fun onPositionChanged(position: Long) {
                currentPosition = position.toFloat()
            }

            override fun onError(error: String) {
                playerError = error
            }
        }

        globalPlayer.addListener(listener)

        onDispose {
            globalPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                currentPosition = globalPlayer.getCurrentPosition().toFloat()
                delay(100)
            }
        }
    }

    val onPlayClick: () -> Unit = {
        val previewUrl = state.song?.previewUrl

        if (previewUrl != null && state.song != null) {
            if (isPlaying) {
                globalPlayer.pause()
                isPlaying = false

//                notificationManager.showNotification(
//                    state.song!!.title,
//                    state.song!!.artist,
//                    false
//                )
            } else {
                globalPlayer.play(songId, previewUrl)
            }
        } else {
            playerError = "No preview available"
        }
    }

    val onPositionChange: (Float) -> Unit = { newPos: Float ->
        globalPlayer.seekTo(newPos.toLong())
    }

    DisposableEffect(Unit) {
        onDispose {
            //notificationManager.dismissNotification()
        }
    }


    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SongDetailsEffect.LogDetailsOpened -> {
                    analyticsManager.logSongDetailsOpened(effect.songId, effect.title)
                }

                is SongDetailsEffect.ReviewDeleted -> {
                    viewModel.initialize(songId)
                }

                is SongDetailsEffect.ShowError -> {
                }
            }
        }
    }

    DeleteConfirmDialog(
        isVisible = state.showDeleteConfirmation,
        title = stringResource(R.string.delete_review),
        message = stringResource(R.string.this_action_cannot_be_undone_your_review_will_be_permanently_deleted),
        onConfirm = { viewModel.onEvent(SongDetailsEvent.OnConfirmDelete) },
        onDismiss = { viewModel.onEvent(SongDetailsEvent.OnCancelDelete) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.song?.title ?: stringResource(R.string.song_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { onReviewAddClick(songId) }) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },

        ) { padding ->
        when {
            state.isLoading -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }

            state.error != null -> {
                ErrorContent(
                    error = state.error ?: "",
                    onRetry = { viewModel.onEvent(SongDetailsEvent.OnRetry) },
                    modifier = Modifier.padding(padding)
                )
            }

            state.song != null -> {
                SongDetailsContent(
                    song = state.song!!,
                    userReview = state.userReview,
                    otherReviews = state.otherReviews,
                    onDeleteClick = { reviewId ->
                        viewModel.onEvent(
                            SongDetailsEvent.OnDeleteReviewClick(reviewId)
                        )
                    },
                    onReviewDetailsClick = onReviewDetailsClick,
                    coverSize = coverSize,
                    scrollState = scrollState,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    playerError = playerError,
                    isLoadingPreview = isLoadingPreview,
                    onPlayClick = onPlayClick,
                    onPositionChange = onPositionChange,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = error, color = Color.Red, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}
