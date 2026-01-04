package ru.kpfu.itis.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.core.navigation.Routes
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

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Any,
    viewModelFactory: ViewModelProvider.Factory,
    analyticsManager: AnalyticsManager,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Routes.Auth> {
            val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Routes.Search) {
                        popUpTo(Routes.Search) { inclusive = true }
                    }
                }
            )
        }

        composable<Routes.Profile> {
            val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
            ProfileScreen(
                viewModel = profileViewModel,
                onOpenReviewDetails = { reviewId ->
                    navController.navigate(Routes.ReviewDetails(reviewId = reviewId))
                },
                onLogout = {
                    navController.navigate(Routes.Auth)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        //popUpTo(Routes.Auth) { saveState = true }
                        launchSingleTop = true
                        //restoreState = true
                    }
                },
            )
        }

        composable<Routes.Search> {
            val searchViewModel: SearchViewModel = viewModel(factory = viewModelFactory)

            SearchScreen(
                viewModel = searchViewModel,
                onOpenDetails = { songId ->
                    navController.navigate(
                        Routes.SongDetails(songId = songId)
                    )
                },
                analyticsManager = analyticsManager,
                onNavigate = { route ->
                    navController.navigate(route) {
                        //popUpTo(Routes.Auth) { saveState = true }
                        launchSingleTop = true
                        //restoreState = true
                    }
                },
            )
        }
        composable<Routes.SongDetails> { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: return@composable
            val songDetailsViewModel: SongDetailsViewModel = viewModel(factory = viewModelFactory)

            SongDetailsScreen(
                viewModel = songDetailsViewModel,
                songId = songId,
                onBack = { navController.popBackStack() },
                onReviewAddClick = { songId ->
                    navController.navigate(
                        Routes.ReviewAdd(songId = songId)
                    )
                },
                onReviewDetailsClick = { reviewId ->
                    navController.navigate(
                        Routes.ReviewDetails(reviewId = reviewId)
                    )
                },
                analyticsManager = analyticsManager,
            )
        }
        composable<Routes.ReviewAdd> { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: return@composable
            val reviewAddViewModel: ReviewAddViewModel = viewModel(factory = viewModelFactory)
            ReviewAddScreen(
                viewModel = reviewAddViewModel,
                songId = songId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Routes.ReviewDetails> { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: return@composable
            val reviewDetailsViewModel: ReviewDetailsViewModel =
                viewModel(factory = viewModelFactory)
            ReviewDetailsScreen(
                viewModel = reviewDetailsViewModel,
                reviewId = reviewId,
                onBack = { navController.popBackStack() },
                onReviewDeleted = {
                    navController.popBackStack()
                }
            )
        }
    }
}