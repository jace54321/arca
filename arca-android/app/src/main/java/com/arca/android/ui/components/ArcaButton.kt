package com.arca.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.ArcaTheme
import com.arca.android.ui.theme.UbuntuFontFamily

/**
 * Primary CTA button — red background, dark text.
 */
@Composable
fun ArcaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "buttonScale"
    )

    Button(
        onClick = {
            if (!isLoading) onClick()
        },
        modifier = modifier
            .height(56.dp)
            .scale(scale),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = ArcaColors.Primary,
            contentColor = ArcaColors.OnPrimary,
            disabledContainerColor = ArcaColors.Primary.copy(alpha = 0.35f),
            disabledContentColor = ArcaColors.OnPrimary.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ArcaColors.OnPrimary,
                strokeWidth = 2.dp,
            )
            Spacer(Modifier.width(8.dp))
        }
        if (icon != null && !isLoading) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = if (isLoading) "LOADING…" else text.uppercase(),
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.02.sp,
        )
    }
}

/**
 * Secondary button — transparent with border.
 */
@Composable
fun ArcaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ArcaColors.TextPrimary,
            disabledContentColor = ArcaColors.TextMuted,
        ),
        border = BorderStroke(1.dp, ArcaColors.Border),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}

/**
 * Danger button — transparent with error-color border and text.
 */
@Composable
fun ArcaDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ArcaColors.Error,
        ),
        border = BorderStroke(1.dp, ArcaColors.Error),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = ArcaColors.Error,
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}
