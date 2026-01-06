package ru.kpfu.itis.profile.impl.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kpfu.itis.core.navigation.NavKey
import ru.kpfu.itis.core.navigation.Navigator
import ru.kpfu.itis.core.navigation.bottom.BottomNavigation
import ru.kpfu.itis.profile.impl.R
import ru.kpfu.itis.profile.impl.presentation.components.ExpandableSection
import ru.kpfu.itis.profile.impl.presentation.components.ProfileHeader
import ru.kpfu.itis.profile.impl.presentation.components.ReviewsSection

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navigator: Navigator,
    currentRoute: NavKey,
    onOpenReviewDetails: (reviewId: String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ProfileEvent.LoadProfile)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is ProfileEffect.NavigateToReviewDetails -> onOpenReviewDetails(effect.reviewId)
                ProfileEffect.Logout -> {
                    navigator.logout()
                }
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.onEvent(ProfileEvent.LoadProfile) }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { stringResource(R.string.profile) },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ProfileEvent.Logout) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.logout),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LoadingContent()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (state.user != null) {
                        item {
                            ProfileHeader(
                                user = state.user!!,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        }
                    }

                    item {
                        ExpandableSection(
                            title = stringResource(R.string.my_reviews),
                            count = state.reviewsCount,
                            isExpanded = state.isReviewsExpanded,
                            modifier = Modifier.fillMaxWidth(),
                            onExpandChanged = {
                                viewModel.onEvent(ProfileEvent.ToggleReviewsExpanded)
                            },
                            icon = Icons.Default.KeyboardArrowDown,
                            content = {
                                ReviewsSection(
                                    reviews = state.reviews,
                                    isLoading = state.isReviewsLoading,
                                    hasMore = state.reviews.size < state.reviewsCount,
                                    onReviewClick = { reviewId ->
                                        viewModel.onEvent(ProfileEvent.OnReviewClick(reviewId))
                                    },
                                    onLoadMore = {
                                        viewModel.onEvent(ProfileEvent.LoadMoreReviews)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    }

                    item {
                        Box(modifier = Modifier.padding(bottom = 16.dp))
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
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