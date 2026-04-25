package com.arca.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Arca border radius tokens from the design brief.
 */
val ArcaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // --radius-sm: badges, chips
    small = RoundedCornerShape(6.dp),        // --radius-md: inputs, buttons
    medium = RoundedCornerShape(10.dp),       // --radius-lg: cards, modals
    large = RoundedCornerShape(16.dp),        // --radius-xl: bottom sheets
    extraLarge = RoundedCornerShape(9999.dp), // --radius-full: avatars, pills
)
