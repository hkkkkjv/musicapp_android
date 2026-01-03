package ru.kpfu.itis.song.impl.presentation.details


import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import ru.kpfu.itis.core.media.GlobalMusicPlayer
import ru.kpfu.itis.core.media.PlayerNotificationManager
import ru.kpfu.itis.core.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.song.api.Song
import ru.kpfu.itis.song.api.SongSource
import ru.kpfu.itis.song.impl.R

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailsScreen(
    viewModel: SongDetailsViewModel,
    songId: String,
    onBack: () -> Unit,
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
        targetValue = if (scrollState.value < 100) 340.dp else 150.dp,
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
            }
        }
    }

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
                )
            )
        }
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

@Composable
private fun SongDetailsContent(
    song: Song,
    coverSize: Dp,
    scrollState: ScrollState,
    isPlaying: Boolean,
    currentPosition: Float,
    duration: Float,
    playerError: String?,
    isLoadingPreview: Boolean,
    onPlayClick: () -> Unit,
    onPositionChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverSize),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = song.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(coverSize - 20.dp)
                    .height(coverSize - 20.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = song.artist,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            if (song.album != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.album_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = song.album!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (song.durationSec != null) {
                Spacer(Modifier.height(4.dp))
                val minutes = song.durationSec!! / 60
                val seconds = song.durationSec!! % 60
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.duration_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$minutes:${seconds.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (song.releaseDate != null) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.released_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = song.releaseDate!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (song.source == SongSource.DEEZER && song.previewUrl != null) {
            Spacer(Modifier.height(24.dp))
            PlayerSection(
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                onPlayClick = onPlayClick,
                onPositionChange = onPositionChange,
                modifier = Modifier.padding(horizontal = 16.dp),
                playerError = playerError,
                isLoadingPreview = isLoadingPreview
            )
        }

        if (!song.lyrics.isNullOrEmpty()) {
            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.lyrics_title),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = song.lyrics!!,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                        .padding(16.dp),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.song_details_title) + song.source,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PlayerSection(
    isPlaying: Boolean,
    currentPosition: Float,
    duration: Float,
    playerError: String?,
    isLoadingPreview: Boolean,
    onPlayClick: () -> Unit,
    onPositionChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoadingPreview) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(
                            R.string.play
                        ),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (playerError != null) {
            Text(
                text = playerError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        if (duration > 0 && !isLoadingPreview) {
            Slider(
                value = currentPosition,
                onValueChange = onPositionChange,
                valueRange = 0f..duration,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition.toLong()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatTime(duration.toLong()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else if (!isLoadingPreview) {
            Text(
                text = stringResource(R.string.initializing_preview),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.preview_info),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}


private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}