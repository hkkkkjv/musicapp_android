package ru.kpfu.itis.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.ui.NavDisplay
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.core.navigation.NavKey
import ru.kpfu.itis.core.navigation.Navigator
import ru.kpfu.itis.core.navigation.ViewModelStoreProvider

@Composable
fun MainContent(
    startDestination: NavKey,
    viewModelFactory: ViewModelProvider.Factory,
    analyticsManager: AnalyticsManager,
) {
    val viewModelStoreProvider = remember { ViewModelStoreProvider() }
    val navigator = remember(startDestination) {
        Navigator(
            startRoute = startDestination,
            viewModelStoreProvider = viewModelStoreProvider
        )
    }

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.remove() },
        entryProvider = createEntryProvider(
            navigator = navigator,
            viewModelFactory = viewModelFactory,
            analyticsManager = analyticsManager,
            viewModelStoreProvider = viewModelStoreProvider
        )
    )
}