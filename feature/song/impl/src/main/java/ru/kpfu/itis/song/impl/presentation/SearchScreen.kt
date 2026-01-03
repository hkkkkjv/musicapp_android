package ru.kpfu.itis.song.impl.presentation

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.kpfu.itis.core.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.song.api.Song
import ru.kpfu.itis.song.api.SongSource
import ru.kpfu.itis.song.impl.R
import androidx.compose.ui.res.stringResource

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onOpenDetails: (String) -> Unit,
    analyticsManager: AnalyticsManager
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var isSearchFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val searchBoxPadding by animateDpAsState(
        targetValue = if (isSearchFocused || state.query.isNotEmpty()) 32.dp else 400.dp,
        animationSpec = tween(durationMillis = 300),
        label = "search_box_animation"
    )
    val filteredSongs = remember(state.results, state.selectedSource) {
        if (state.selectedSource == null) {
            state.results
        } else {
            state.results.filter { it.source == state.selectedSource }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SearchEffect.LogSearch -> {
                    analyticsManager.logSearchQuery(effect.query)
                }

                is SearchEffect.LogSongClick -> {
                    analyticsManager.logSongSelected(effect.songId, effect.title)
                }

                is SearchEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { nav ->
            when (nav) {
                is SearchNavigation.OpenSongDetails -> onOpenDetails(nav.songId)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = searchBoxPadding)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { newQuery ->
                    viewModel.onEvent(SearchEvent.OnQueryChanged(newQuery))
                    if (newQuery.isEmpty()) {
                        viewModel.onEvent(SearchEvent.OnClearResults)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isSearchFocused = focusState.isFocused
                    },
                label = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
                leadingIcon = {
                    if (!state.query.isNotEmpty()) {
                        Icon(Icons.Default.Search, null)
                    }
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                viewModel.onEvent(SearchEvent.OnSearchClicked(state.query))
                            },
                            enabled = !state.isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_hint),
                                tint = if (!state.isLoading) MaterialTheme.colorScheme.primary
                                else Color.Gray
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.onEvent(SearchEvent.OnSearchClicked(state.query))
                    }
                )
            )
        }
        Spacer(Modifier.height(16.dp))

        if (state.hasResults) {

            SourceFilterButtons(
                selectedSource = state.selectedSource,
                onSourceSelected = { source ->
                    viewModel.onEvent(SearchEvent.OnSourceFilterChanged(source))
                }
            )
            Spacer(Modifier.height(16.dp))
        }
        when {
            state.isLoading -> {
                LoadingContent()
            }

            state.hasError -> {
                ErrorContent(
                    error = state.error ?: "",
                    onRetry = { viewModel.onEvent(SearchEvent.OnRetry) },
                    onDismiss = { viewModel.onEvent(SearchEvent.OnClearError) }
                )
            }

            state.hasResults -> {
                ResultsContent(
                    songs = filteredSongs,
                    onSongClicked = {
                        viewModel.onEvent(SearchEvent.OnSongClicked(it))
                    }
                )
            }
        }
    }
}

@Composable
private fun SourceFilterButtons(
    selectedSource: SongSource?,
    onSourceSelected: (SongSource) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        SourceButton(
            label = stringResource(R.string.source_genius),
            isSelected = selectedSource == SongSource.GENIUS,
            onClick = { onSourceSelected(SongSource.GENIUS) },
            modifier = Modifier.weight(1f)
        )
        SourceButton(
            label = stringResource(R.string.source_deezer),
            isSelected = selectedSource == SongSource.DEEZER,
            onClick = { onSourceSelected(SongSource.DEEZER) },
            modifier = Modifier.weight(1f)
        )
    }

}

@Composable
private fun SourceButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error, color = Color.Red)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
        Button(onClick = onDismiss) {
            Text(stringResource(R.string.dismiss))
        }
    }
}

@Composable
private fun EmptyContent(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.no_results, query))
    }
}

@Composable
private fun ResultsContent(
    songs: List<Song>,
    onSongClicked: (Song) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(songs.size) { songId ->
            SongItem(
                song = songs[songId],
                onClick = { onSongClicked(songs[songId]) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            modifier = Modifier
                .width(86.dp)
                .height(86.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        if (song.source == SongSource.DEEZER && song.previewUrl != null) {
            Text(
                text = stringResource(R.string.icon_music),
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
