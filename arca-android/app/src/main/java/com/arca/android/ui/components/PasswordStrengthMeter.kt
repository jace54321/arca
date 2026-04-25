package com.arca.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily
import com.arca.android.util.PasswordGenerator

/**
 * 4-segment horizontal password strength meter.
 * Segments fill from red → green as strength increases.
 */
@Composable
fun PasswordStrengthMeter(
    password: String,
    modifier: Modifier = Modifier,
) {
    val strength = PasswordGenerator.calculateStrength(password)
    val label = PasswordGenerator.strengthLabel(strength)

    val segmentColors = listOf(
        if (strength >= 1) strengthColor(strength) else ArcaColors.Border,
        if (strength >= 2) strengthColor(strength) else ArcaColors.Border,
        if (strength >= 3) strengthColor(strength) else ArcaColors.Border,
        if (strength >= 4) strengthColor(strength) else ArcaColors.Border,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 4 segment bars
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            segmentColors.forEach { color ->
                val animatedColor by animateColorAsState(
                    targetValue = color,
                    animationSpec = tween(300),
                    label = "strengthColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(animatedColor)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Strength label
        if (password.isNotEmpty()) {
            Text(
                text = label,
                color = strengthColor(strength),
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }
    }
}

private fun strengthColor(strength: Int): Color = when (strength) {
    0 -> ArcaColors.Error
    1 -> ArcaColors.Error
    2 -> ArcaColors.Warning
    3 -> ArcaColors.Success
    else -> ArcaColors.Success
}
