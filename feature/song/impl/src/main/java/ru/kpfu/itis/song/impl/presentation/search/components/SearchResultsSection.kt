package ru.kpfu.itis.song.impl.presentation.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.song.impl.R

@Composable
fun SearchResultsSection(
    isLoading: Boolean,
    hasError: Boolean,
    error: String?,
    hasResults: Boolean,
    songs: ImmutableList<Song>,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    onSongClicked: (Song) -> Unit,
) {
    when {
        isLoading -> LoadingContent()
        hasError -> ErrorContent(
            error = error ?: "",
            onRetry = onRetry,
            onDismiss = onDismissError
        )

        hasResults -> ResultsContent(
            songs = songs,
            onSongClicked = onSongClicked
        )

        else -> EmptyContent()
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
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onDismiss) {
            Text(stringResource(R.string.dismiss))
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

    }
}

@Composable
private fun ResultsContent(
    songs: List<Song>,
    onSongClicked: (Song) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(songs.size) { index ->
            SongItem(
                song = songs[index],
                onClick = { onSongClicked(songs[index]) }
            )
            HorizontalDivider()
        }
    }
}