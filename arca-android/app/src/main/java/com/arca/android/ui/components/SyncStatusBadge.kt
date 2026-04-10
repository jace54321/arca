package com.arca.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

/**
 * Sync status badge — pill-shaped indicator showing current sync state.
 * The "Syncing" state has a rotating icon animation.
 */
@Composable
fun SyncStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val (icon, color, label) = when (status.lowercase()) {
        "synced" -> Triple(Icons.Filled.CloudDone, ArcaColors.Success, "Synced")
        "syncing" -> Triple(Icons.Filled.Sync, ArcaColors.Info, "Syncing…")
        "pending" -> Triple(Icons.Filled.CloudUpload, ArcaColors.Warning, "Pending")
        "offline" -> Triple(Icons.Filled.CloudOff, ArcaColors.TextMuted, "Offline")
        "error" -> Triple(Icons.Filled.SyncProblem, ArcaColors.Error, "Sync Failed")
        else -> Triple(Icons.Filled.Cloud, ArcaColors.TextMuted, status)
    }

    // Spin animation for "syncing" state
    val infiniteTransition = rememberInfiniteTransition(label = "syncSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "syncRotation"
    )

    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(9999.dp),
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier
                .size(14.dp)
                .then(
                    if (status.lowercase() == "syncing") Modifier.rotate(rotation)
                    else Modifier
                ),
        )

        if (!compact) {
            Text(
                text = label,
                color = color,
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
        }
    }
}
