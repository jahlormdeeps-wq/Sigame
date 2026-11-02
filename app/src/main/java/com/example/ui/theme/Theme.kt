package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = GhanaGreenLight,
    onPrimary = Color.Black,
    primaryContainer = GhanaGreenDark,
    onPrimaryContainer = GhanaGreenContainer,
    secondary = GhanaGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = OnGhanaGoldContainer,
    onSecondaryContainer = GhanaGoldContainer,
    tertiary = GhanaRedTerracotta,
    background = GhanaDarkBackground,
    surface = GhanaDarkSurface,
    surfaceVariant = GhanaDarkSurfaceVariant,
    onBackground = GhanaDarkTextPrimary,
    onSurface = GhanaDarkTextPrimary,
    onSurfaceVariant = GhanaDarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = GhanaGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GhanaGreenContainer,
    onPrimaryContainer = OnGhanaGreenContainer,
    secondary = GhanaGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GhanaGoldContainer,
    onSecondaryContainer = OnGhanaGoldContainer,
    tertiary = GhanaRedTerracotta,
    onTertiary = Color.White,
    background = GhanaBackgroundWarm,
    surface = GhanaSurfaceWarm,
    surfaceVariant = GhanaSurfaceVariant,
    onBackground = GhanaTextPrimary,
    onSurface = GhanaTextPrimary,
    onSurfaceVariant = GhanaTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand aesthetic
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
