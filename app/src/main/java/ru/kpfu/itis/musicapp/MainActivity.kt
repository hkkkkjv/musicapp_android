package ru.kpfu.itis.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.kpfu.itis.auth.api.presentation.AuthState
import ru.kpfu.itis.core.data.network.firebase.analytics.AnalyticsManager
import ru.kpfu.itis.musicapp.navigation.AppNavGraph
import ru.kpfu.itis.musicapp.navigation.Routes
import ru.kpfu.itis.musicapp.ui.theme.MusicAppTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as MusicApp).appComponent.inject(this)

        setContent {
            val authStateViewModel: AuthStateViewModel = viewModel(factory = viewModelFactory)
            val authState by authStateViewModel.authState.collectAsState()

            val startDestination = if (authState is AuthState.Authenticated) {
                Routes.Search
            } else {
                Routes.Auth
            }

            MusicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        viewModelFactory = viewModelFactory,
                        analyticsManager = analyticsManager,
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! This is my music app!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicAppTheme {
        Greeting("User")
    }
}
