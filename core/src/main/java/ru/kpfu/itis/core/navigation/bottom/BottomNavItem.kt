package ru.kpfu.itis.core.navigation.bottom

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import ru.kpfu.itis.core.R
import ru.kpfu.itis.core.navigation.NavKey


sealed class BottomNavItem(
    val route: NavKey,
    val icon: ImageVector,
    @param:StringRes val labelResId: Int
) {
    data object SearchNav : BottomNavItem(
        NavKey.Search,
        Icons.Filled.Search,
        R.string.nav_search
    )

    data object ProfileNav : BottomNavItem(
        NavKey.Profile,
        Icons.Filled.Person,
        R.string.nav_profile
    )

    companion object {
        fun items() = listOf(SearchNav, ProfileNav)
    }
}