package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CampusWellLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = Color(0xFFFCFAFF),
    secondary = SecondarySlate,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryGreen,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainerGreen,
    onTertiaryContainer = OnTertiaryContainer,
    background = SurfaceCanvas,
    onBackground = OnSurfaceDark,
    surface = SurfaceCanvas,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = OutlineGrey,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFF93000A)
)

private val CampusWellDarkColorScheme = darkColorScheme(
    primary = PrimaryFixedDim,
    onPrimary = Color(0xFF001945),
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = Color(0xFFFCFAFF),
    secondary = SecondaryFixed,
    onSecondary = Color(0xFF051A3F),
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryFixedDim,
    onTertiary = Color(0xFF002113),
    tertiaryContainer = TertiaryContainerGreen,
    onTertiaryContainer = OnTertiaryContainer,
    background = Color(0xFF0B1426),
    onBackground = Color(0xFFEAF1FF),
    surface = Color(0xFF0E1A33),
    onSurface = Color(0xFFEAF1FF),
    surfaceVariant = Color(0xFF182E5A),
    onSurfaceVariant = Color(0xFFC3C6D5),
    surfaceContainerLowest = Color(0xFF122040),
    surfaceContainerLow = Color(0xFF15264B),
    surfaceContainer = Color(0xFF182E5A),
    surfaceContainerHigh = Color(0xFF1E3A6E),
    surfaceContainerHighest = Color(0xFF244684),
    outline = OutlineGrey,
    outlineVariant = OutlineVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CampusWellDarkColorScheme else CampusWellLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
