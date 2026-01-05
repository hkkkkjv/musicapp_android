package ru.kpfu.itis.song.impl.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.core.domain.models.SongSource
import ru.kpfu.itis.core.navigation.bottom.BottomNavigation
import ru.kpfu.itis.core.navigation.NavKey
import ru.kpfu.itis.core.navigation.Navigator
import ru.kpfu.itis.core.presentation.components.InfoDialog
import ru.kpfu.itis.song.impl.R
import ru.kpfu.itis.song.impl.presentation.search.components.SearchInputSection
import ru.kpfu.itis.song.impl.presentation.search.components.SearchResultsSection

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navigator: Navigator,
    currentRoute: NavKey,
    onOpenDetails: (String) -> Unit,
    analyticsManager: AnalyticsManager,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val searchScreenState = rememberSearchScreenState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SearchEffect.LogSearch -> analyticsManager.logSearchQuery(effect.query)
                is SearchEffect.LogSongClick ->
                    analyticsManager.logSongSelected(effect.songId, effect.title)

                is SearchEffect.ShowToast -> {
                    searchScreenState.showToast(effect.message)
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
    val filteredSongs = remember(state.results, state.selectedSource) {
        val filtered = if (state.selectedSource == null) {
            state.results
        } else {
            state.results.filter { it.source == state.selectedSource }
        }
        filtered.toImmutableList()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route is NavKey.Search || route is NavKey.Profile) {
                        navigator.backStack.clear()
                        navigator.backStack.add(route)
                    } else {
                        navigator.add(route)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InfoDialog(
                isVisible = searchScreenState.showToastDialog,
                title = stringResource(R.string.info),
                message = searchScreenState.toastMessage,
                onDismiss = { searchScreenState.hideToast() }
            )
            SearchInputSection(
                query = state.query,
                isLoading = state.isLoading,
                isFocused = searchScreenState.isSearchFocused,
                onQueryChanged = { newQuery ->
                    viewModel.onEvent(SearchEvent.OnQueryChanged(newQuery))
                    if (newQuery.isEmpty()) {
                        viewModel.onEvent(SearchEvent.OnClearResults)
                    }
                },
                onFocusChanged = { searchScreenState.updateSearchFocus(it) },
                onSearch = {
                    searchScreenState.hideKeyboard()
                    viewModel.onEvent(SearchEvent.OnSearchClicked(state.query))
                }
            )
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

            SearchResultsSection(
                isLoading = state.isLoading,
                hasError = state.hasError,
                error = state.error,
                hasResults = state.hasResults,
                songs = filteredSongs,
                onRetry = { viewModel.onEvent(SearchEvent.OnRetry) },
                onDismissError = { viewModel.onEvent(SearchEvent.OnClearError) },
                onSongClicked = { viewModel.onEvent(SearchEvent.OnSongClicked(it)) }
            )
        }
    }
}

@Composable
private fun rememberSearchScreenState(): SearchScreenState {
    return remember {
        SearchScreenState()
    }
}

private class SearchScreenState {
    var showToastDialog by mutableStateOf(false)
        private set

    var toastMessage by mutableStateOf("")
        private set

    var isSearchFocused by mutableStateOf(false)
        private set

    fun showToast(message: String) {
        toastMessage = message
        showToastDialog = true
    }

    fun hideToast() {
        showToastDialog = false
    }

    fun updateSearchFocus(focused: Boolean) {
        isSearchFocused = focused
    }

    fun hideKeyboard() {
        isSearchFocused = false
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
