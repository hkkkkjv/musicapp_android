package ru.kpfu.itis.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.kpfu.itis.core.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.impl.presentation.mvi.AuthViewModel
import ru.kpfu.itis.impl.presentation.screens.AuthScreen
import ru.kpfu.itis.song.impl.presentation.SearchScreen
import ru.kpfu.itis.song.impl.presentation.SearchViewModel
import ru.kpfu.itis.song.impl.presentation.details.SongDetailsScreen
import ru.kpfu.itis.song.impl.presentation.details.SongDetailsViewModel

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

        composable<Routes.Search> {
            val searchViewModel: SearchViewModel = viewModel(factory = viewModelFactory)

            SearchScreen(
                viewModel = searchViewModel,
                onOpenDetails = { songId ->
                    navController.navigate(
                        Routes.SongDetails(songId = songId)
                    )
                },
                analyticsManager = analyticsManager

            )
        }
        composable<Routes.SongDetails> { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId") ?: return@composable
            val songDetailsViewModel: SongDetailsViewModel = viewModel(factory = viewModelFactory)

            SongDetailsScreen(
                viewModel = songDetailsViewModel,
                songId = songId,
                onBack = { navController.popBackStack() },
                analyticsManager = analyticsManager,
            )
        }
    }
}