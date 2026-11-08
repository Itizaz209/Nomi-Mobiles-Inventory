package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Navy950,
    primaryContainer = Navy700,
    onPrimaryContainer = CyanGlow,
    secondary = BlueSky,
    onSecondary = Navy950,
    secondaryContainer = Navy800,
    onSecondaryContainer = BlueSky,
    tertiary = GoldAmber,
    onTertiary = Navy950,
    background = Navy900,
    onBackground = DarkTextPrimary,
    surface = Navy850,
    onSurface = DarkTextPrimary,
    surfaceVariant = Navy800,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = RedDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7F8),
    onPrimaryContainer = CyanDark,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVar,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = RedDanger,
    onError = Color.White
)

@Composable
fun NomiMobilesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
