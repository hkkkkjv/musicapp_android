package ru.kpfu.itis.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.kpfu.itis.auth.api.AuthEvent
import ru.kpfu.itis.auth.api.AuthState
import ru.kpfu.itis.impl.presentation.screens.AuthScreen
import ru.kpfu.itis.impl.presentation.mvi.AuthViewModel
import ru.kpfu.itis.musicapp.ui.theme.HomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.state.collectAsState()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> Routes.Home
        else -> Routes.Auth
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Routes.Auth> {
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Routes.Home> {
            HomeScreen(
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.onEvent(AuthEvent.OnLogout)
                    navController.navigate(Routes.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}