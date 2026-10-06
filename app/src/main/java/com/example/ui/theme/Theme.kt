package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DrumAmberPrimary,
    onPrimary = DrumAmberOnPrimary,
    primaryContainer = DrumAmberContainer,
    onPrimaryContainer = DrumAmberOnContainer,
    secondary = DrumCyanSecondary,
    onSecondary = DrumCyanOnSecondary,
    secondaryContainer = DrumCyanContainer,
    onSecondaryContainer = DrumCyanOnContainer,
    tertiary = DrumKickOrange,
    onTertiary = DrumKickOnOrange,
    tertiaryContainer = DrumKickContainer,
    background = StageDarkBackground,
    onBackground = StageDarkOnBackground,
    surface = StageDarkSurface,
    onSurface = StageDarkOnSurface,
    surfaceVariant = StageDarkSurfaceVariant,
    onSurfaceVariant = StageDarkOnSurfaceVariant,
    outline = StageDarkOutline
)

// Drums app is fundamentally stage-designed with dark background for low glare and high contrast
private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep high-contrast custom studio stage palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
