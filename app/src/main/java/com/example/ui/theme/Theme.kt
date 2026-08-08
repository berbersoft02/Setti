package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HighContrastLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = OnNavyPrimary,
    primaryContainer = NavyContainer,
    onPrimaryContainer = Color(0xFFD5E3FF),
    secondary = TealSecondary,
    onSecondary = Color.White,
    secondaryContainer = TealContainer,
    onSecondaryContainer = OnTealContainer,
    tertiary = GoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = OnGoldContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceDark,
    surface = Color.White,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    outline = HighContrastBorder
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF001E40),
    primaryContainer = Color(0xFF003366),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF4DD0E1),
    onSecondary = Color(0xFF00363A),
    secondaryContainer = Color(0xFF004F4F),
    onSecondaryContainer = Color(0xFFE0F7FA),
    tertiary = GoldContainer,
    onTertiary = OnGoldContainer,
    background = Color(0xFF10141A),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF18202C),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF232D3C),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF90CAF9)
)

@Composable
fun SettiFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
