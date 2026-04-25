package com.arca.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

// ── Google Fonts provider ──────────────────────────────────────────────────────

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.arca.android.R.array.com_google_android_gms_fonts_certs
)

// ── Ubuntu — primary UI font ──────────────────────────────────────────────────

private val ubuntuFont = GoogleFont("Ubuntu")

val UbuntuFontFamily = FontFamily(
    Font(googleFont = ubuntuFont, fontProvider = fontProvider, weight = FontWeight.Light),
    Font(googleFont = ubuntuFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = ubuntuFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = ubuntuFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

// ── JetBrains Mono — passwords & technical data ───────────────────────────────

private val jetBrainsMonoFont = GoogleFont("JetBrains Mono")

val JetBrainsMonoFontFamily = FontFamily(
    Font(googleFont = jetBrainsMonoFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = jetBrainsMonoFont, fontProvider = fontProvider, weight = FontWeight.Medium),
)

// ── Arca typography scale ─────────────────────────────────────────────────────

val ArcaTypography = Typography(
    // display-xl: Screen titles (rare)
    displayLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = (32 * 1.2).sp,
        letterSpacing = (-0.02).sp,
    ),
    // display-lg: Page headings
    displayMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = (24 * 1.3).sp,
        letterSpacing = (-0.01).sp,
    ),
    // display-md: Section headings, modal titles
    displaySmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = (20 * 1.4).sp,
    ),
    // Headline mappings (unused in design brief, but sensible defaults)
    headlineLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = (24 * 1.3).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = (20 * 1.4).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = (18 * 1.4).sp,
    ),
    // Title
    titleLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = (20 * 1.4).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = (16 * 1.5).sp,
    ),
    titleSmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = (14 * 1.5).sp,
    ),
    // body-lg: Primary body copy
    bodyLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = (16 * 1.6).sp,
    ),
    // body-md: Default UI text
    bodyMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = (14 * 1.5).sp,
    ),
    // body-sm: Captions, timestamps, helper text
    bodySmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = (12 * 1.4).sp,
        letterSpacing = 0.01.sp,
    ),
    // label: Input labels, table headers
    labelLarge = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = (14 * 1.3).sp,
        letterSpacing = 0.04.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = (13 * 1.3).sp,
        letterSpacing = 0.04.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = (12 * 1.3).sp,
        letterSpacing = 0.04.sp,
    ),
)

// ── Monospace styles for passwords/technical data ─────────────────────────────

object MonoStyles {
    val monoMd = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = (14 * 1.5).sp,
    )
    val monoSm = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = (12 * 1.4).sp,
    )
}
