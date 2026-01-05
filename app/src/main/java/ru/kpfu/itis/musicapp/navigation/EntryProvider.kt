package ru.kpfu.itis.musicapp.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.core.navigation.NavKey
import ru.kpfu.itis.core.navigation.Navigator
import ru.kpfu.itis.core.navigation.ViewModelStoreProvider
import ru.kpfu.itis.impl.presentation.mvi.AuthViewModel
import ru.kpfu.itis.impl.presentation.screens.AuthScreen
import ru.kpfu.itis.profile.impl.presentation.ProfileScreen
import ru.kpfu.itis.profile.impl.presentation.ProfileViewModel
import ru.kpfu.itis.review.impl.presentation.add.ReviewAddScreen
import ru.kpfu.itis.review.impl.presentation.add.ReviewAddViewModel
import ru.kpfu.itis.review.impl.presentation.details.ReviewDetailsScreen
import ru.kpfu.itis.review.impl.presentation.details.ReviewDetailsViewModel
import ru.kpfu.itis.song.impl.presentation.details.SongDetailsScreen
import ru.kpfu.itis.song.impl.presentation.details.SongDetailsViewModel
import ru.kpfu.itis.song.impl.presentation.search.SearchScreen
import ru.kpfu.itis.song.impl.presentation.search.SearchViewModel

@Suppress("LongMethod")
fun createEntryProvider(
    navigator: Navigator,
    viewModelFactory: ViewModelProvider.Factory,
    analyticsManager: AnalyticsManager,
    viewModelStoreProvider: ViewModelStoreProvider
) = entryProvider<NavKey> {
    entry<NavKey.Auth> { key ->
        val owner = viewModelStoreProvider.get(key)
        val authViewModel: AuthViewModel = viewModel(
            factory = viewModelFactory,
            viewModelStoreOwner = owner
        )
        ScopedViewModelOwner {
            val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navigator.login()
                    navigator.resetToHome()
                }
            )
        }
    }

    entry<NavKey.Search> { key ->
        val owner = viewModelStoreProvider.get(key)
        val searchViewModel: SearchViewModel = viewModel(
            factory = viewModelFactory,
            viewModelStoreOwner = owner
        )
        SearchScreen(
            viewModel = searchViewModel,
            onOpenDetails = { songId ->
                navigator.add(NavKey.SongDetails(songId))
            },
            navigator = navigator,
            currentRoute = key,
            analyticsManager = analyticsManager,
        )
    }

    entry<NavKey.Profile> { key ->
        val owner = viewModelStoreProvider.get(key)
        val profileViewModel: ProfileViewModel = viewModel(
            factory = viewModelFactory,
            viewModelStoreOwner = owner
        )
        ProfileScreen(
            viewModel = profileViewModel,
            navigator = navigator,
            currentRoute = key,
            onOpenReviewDetails = { reviewId ->
                navigator.add(NavKey.ReviewDetails(reviewId))
            },
        )
    }

    entry<NavKey.SongDetails> { key ->
        val owner = viewModelStoreProvider.get(key)
        val songDetailsViewModel: SongDetailsViewModel = viewModel(
            factory = viewModelFactory,
            key = key.songId,
            viewModelStoreOwner = owner
        )
        SongDetailsScreen(
            viewModel = songDetailsViewModel,
            songId = key.songId,
            onBack = { navigator.remove() },
            onReviewAddClick = { songId ->
                navigator.add(NavKey.ReviewAdd(songId))
            },
            onReviewDetailsClick = { reviewId ->
                navigator.add(NavKey.ReviewDetails(reviewId))
            },
            analyticsManager = analyticsManager
        )
    }

    entry<NavKey.ReviewAdd> { key ->
        val owner = viewModelStoreProvider.get(key)
        val reviewAddViewModel: ReviewAddViewModel =
            viewModel(
                factory = viewModelFactory,
                key = key.songId,
                viewModelStoreOwner = owner
            )
        ReviewAddScreen(
            viewModel = reviewAddViewModel,
            songId = key.songId,
            onBack = { navigator.remove() }
        )
    }

    entry<NavKey.ReviewDetails> { key ->
        val owner = viewModelStoreProvider.get(key)
        val reviewDetailsViewModel: ReviewDetailsViewModel =
            viewModel(
                factory = viewModelFactory,
                key = key.reviewId,
                viewModelStoreOwner = owner
            )
        ReviewDetailsScreen(
            viewModel = reviewDetailsViewModel,
            reviewId = key.reviewId,
            onBack = { navigator.remove() },
            onReviewDeleted = {
                navigator.remove()
            }
        )
    }
}
