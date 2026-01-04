package ru.kpfu.itis.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import ru.kpfu.itis.core.R


sealed class BottomNavItem(
    val route: Any,
    val icon: ImageVector,
    @StringRes
    val labelResId: Int
) {
    object Search : BottomNavItem(
        Routes.Search,
        Icons.Filled.Search,
        labelResId = R.string.nav_search
    )

    object Profile : BottomNavItem(
        Routes.Profile,
        Icons.Filled.Person,
        labelResId = R.string.nav_profile
    )

    companion object {
        fun items() = listOf(Search, Profile)
    }
}