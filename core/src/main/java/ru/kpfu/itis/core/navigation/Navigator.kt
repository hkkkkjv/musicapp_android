package ru.kpfu.itis.core.navigation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

class Navigator(
    startRoute: NavKey,
    private val loginRoute: NavKey = NavKey.Auth,
    private var viewModelStoreProvider: ViewModelStoreProvider? = null
) {
    private var onLoginSuccessRoute: NavKey? = null
    var isLoggedIn = false
        private set

    val backStack = mutableStateListOf(startRoute)

    fun add(route: NavKey) {
        if (route is NavKey.Auth) {
            isLoggedIn = false
        }
        backStack.add(route)
    }

    fun remove() {
        backStack.removeLastOrNull()
    }

    fun goTo(route: NavKey) {
        if (!isLoggedIn && route !is NavKey.Auth) {
            onLoginSuccessRoute = route
            backStack.add(loginRoute)
        } else {
            backStack.add(route)
        }
    }

    fun login() {
        isLoggedIn = true
        onLoginSuccessRoute?.let {
            backStack.add(it)
            onLoginSuccessRoute = null
        }
    }

    fun logout() {
        Log.i("logout","logout")
        isLoggedIn = false
        viewModelStoreProvider!!.clearAllStores()
        backStack.clear()
        backStack.add(NavKey.Auth)
    }

    fun resetToHome() {
        backStack.clear()
        backStack.add(NavKey.Search)
    }
}