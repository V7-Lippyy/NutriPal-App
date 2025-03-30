package com.example.nutripal.ui.common.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = PrimaryColor.copy(alpha = 0.15f),
    onPrimaryContainer = PrimaryColor,
    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = SecondaryColor.copy(alpha = 0.15f),
    onSecondaryContainer = SecondaryColor,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AccentBlue.copy(alpha = 0.15f),
    onTertiaryContainer = AccentBlue,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorColor.copy(alpha = 0.15f),
    onErrorContainer = ErrorColor,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightBackground.copy(alpha = 0.7f),
    onSurfaceVariant = LightOnBackground.copy(alpha = 0.7f),
    outline = LightOnBackground.copy(alpha = 0.3f)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = PrimaryColor.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryColor,
    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = SecondaryColor.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryColor,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AccentBlue.copy(alpha = 0.2f),
    onTertiaryContainer = AccentBlue,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorColor.copy(alpha = 0.2f),
    onErrorContainer = ErrorColor,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkBackground.copy(alpha = 0.7f),
    onSurfaceVariant = DarkOnBackground.copy(alpha = 0.7f),
    outline = DarkOnBackground.copy(alpha = 0.3f)
)

@Composable
fun NutriPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false by default for consistent branding
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Set transparency with a slight color tint from the primary color
            val statusBarColor = if (darkTheme) {
                Color.Transparent.copy(alpha = 0.3f).toArgb()
            } else {
                Color.Transparent.toArgb()
            }

            window.statusBarColor = statusBarColor
            window.navigationBarColor = statusBarColor

            // Set the appearance of system bars
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}