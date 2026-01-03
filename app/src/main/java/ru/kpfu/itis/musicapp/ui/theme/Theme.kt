package ru.kpfu.itis.musicapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BordeauxPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = BordeauxPrimaryLight,
    onPrimaryContainer = TextPrimaryLight,

    secondary = RoseDarkSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = RoseLightSecondary,
    onSecondaryContainer = TextPrimaryLight,

    tertiary = BerryTertiary,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = BerryTertiaryLight,
    onTertiaryContainer = TextPrimaryLight,

    error = ErrorColor,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFCE4EC),
    onErrorContainer = ErrorColor,

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,

    outline = DividerLight,
    outlineVariant = PlaceholderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BordeauxPrimaryDark,
    onPrimary = Color(0xFF2A1A26),
    primaryContainer = Color(0xFF8E2C50),
    onPrimaryContainer = TextPrimaryDark,

    secondary = RoseDarkSecondaryDark,
    onSecondary = Color(0xFF3A1C2A),
    secondaryContainer = Color(0xFF9C4570),
    onSecondaryContainer = TextPrimaryDark,

    tertiary = BerryTertiaryDark,
    onTertiary = Color(0xFF3A1C2A),
    tertiaryContainer = Color(0xFFA6234D),
    onTertiaryContainer = TextPrimaryDark,

    error = ErrorColor,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFDADA),

    background = BackgroundDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    outline = DividerDark,
    outlineVariant = PlaceholderDark
)

@Composable
fun MusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
