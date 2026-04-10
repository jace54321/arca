package com.arca.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Arca design system color tokens.
 * Matches the design brief exactly — dark theme with red accent.
 */
object ArcaColors {
    // ── Backgrounds ──
    val Background = Color(0xFF14181E)        // --color-bg: near-black base
    val Surface = Color(0xFF1F2329)           // --color-surface: cards, modals, inputs
    val SurfaceRaised = Color(0xFF272C33)     // --color-surface-raised: hover, elevated
    val Overlay = Color(0xFF2F353D)           // Level 3: tooltips, context menus

    // ── Borders ──
    val Border = Color(0xFF363C45)            // --color-border: subtle dividers
    val BorderActive = Color(0xFFF90000)      // --color-border-active: focused states

    // ── Primary (brand red) ──
    val Primary = Color(0xFFF90000)           // --color-primary: CTAs, brand accents
    val PrimaryDim = Color(0xFFB30000)        // --color-primary-dim: pressed state
    val PrimaryGlow = Color(0x1FF90000)       // --color-primary-glow: subtle bg tint (12% alpha)

    // ── Text ──
    val TextPrimary = Color(0xFFF1F5F9)       // --color-text-primary: headings, body
    val TextSecondary = Color(0xFF94A3B8)     // --color-text-secondary: labels, captions
    val TextMuted = Color(0xFF475569)         // --color-text-muted: disabled, timestamps

    // ── Semantic ──
    val Success = Color(0xFF10B981)           // --color-success: synced, strong password
    val Warning = Color(0xFFFF4500)           // --color-warning: pending, fair password
    val Error = Color(0xFFEF4444)            // --color-error: errors, weak password
    val Info = Color(0xFF3B82F6)              // --color-info: syncing indicator

    // ── Button text on primary ──
    val OnPrimary = Color(0xFF14181E)         // Dark text on red buttons
}
