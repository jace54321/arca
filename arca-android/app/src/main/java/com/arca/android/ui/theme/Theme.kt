package com.arca.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Custom Arca color scheme holder ────────────────────────────────────────────

data class ArcaColorScheme(
    val background: Color = ArcaColors.Background,
    val surface: Color = ArcaColors.Surface,
    val surfaceRaised: Color = ArcaColors.SurfaceRaised,
    val overlay: Color = ArcaColors.Overlay,
    val border: Color = ArcaColors.Border,
    val borderActive: Color = ArcaColors.BorderActive,
    val primary: Color = ArcaColors.Primary,
    val primaryDim: Color = ArcaColors.PrimaryDim,
    val primaryGlow: Color = ArcaColors.PrimaryGlow,
    val textPrimary: Color = ArcaColors.TextPrimary,
    val textSecondary: Color = ArcaColors.TextSecondary,
    val textMuted: Color = ArcaColors.TextMuted,
    val success: Color = ArcaColors.Success,
    val warning: Color = ArcaColors.Warning,
    val error: Color = ArcaColors.Error,
    val info: Color = ArcaColors.Info,
    val onPrimary: Color = ArcaColors.OnPrimary,
)

private val LocalArcaColors = staticCompositionLocalOf { ArcaColorScheme() }

// ── Material 3 dark scheme mapped to Arca tokens ───────────────────────────────

private val ArcaDarkColorScheme = darkColorScheme(
    primary = ArcaColors.Primary,
    onPrimary = ArcaColors.OnPrimary,
    primaryContainer = ArcaColors.PrimaryDim,
    onPrimaryContainer = ArcaColors.TextPrimary,
    secondary = ArcaColors.TextSecondary,
    onSecondary = ArcaColors.Background,
    background = ArcaColors.Background,
    onBackground = ArcaColors.TextPrimary,
    surface = ArcaColors.Surface,
    onSurface = ArcaColors.TextPrimary,
    surfaceVariant = ArcaColors.SurfaceRaised,
    onSurfaceVariant = ArcaColors.TextSecondary,
    outline = ArcaColors.Border,
    outlineVariant = ArcaColors.Border,
    error = ArcaColors.Error,
    onError = ArcaColors.TextPrimary,
)

// ── ArcaTheme composable ───────────────────────────────────────────────────────

@Composable
fun ArcaTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalArcaColors provides ArcaColorScheme()) {
        MaterialTheme(
            colorScheme = ArcaDarkColorScheme,
            typography = ArcaTypography,
            shapes = ArcaShapes,
            content = content
        )
    }
}

/**
 * Access the Arca-specific color tokens that extend Material 3.
 * Usage: ArcaTheme.colors.primary, ArcaTheme.colors.surfaceRaised, etc.
 */
object ArcaTheme {
    val colors: ArcaColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalArcaColors.current
}
