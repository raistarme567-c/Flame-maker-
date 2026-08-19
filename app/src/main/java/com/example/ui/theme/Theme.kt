package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FlameDarkColorScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = CanvasDark,
    primaryContainer = FlameAmber,
    onPrimaryContainer = CanvasDark,
    secondary = PlasmaCyan,
    onSecondary = CanvasDark,
    secondaryContainer = NeonPurple,
    onSecondaryContainer = TextPrimary,
    tertiary = MatrixGreen,
    background = CanvasDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun FlameMakerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = FlameDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CanvasDark.toArgb()
            window.navigationBarColor = SurfaceDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
