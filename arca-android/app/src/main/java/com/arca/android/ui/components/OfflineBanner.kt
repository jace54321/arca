package com.arca.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

/**
 * Persistent offline banner — non-dismissible strip shown when device has no network.
 */
@Composable
fun OfflineBanner(
    unsyncedCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ArcaColors.Warning.copy(alpha = 0.12f))
            .drawBehind {
                // Left accent border
                drawLine(
                    color = ArcaColors.Warning,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 3.dp.toPx(),
                )
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.WifiOff,
            contentDescription = "Offline",
            tint = ArcaColors.Warning,
            modifier = Modifier.size(16.dp),
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = if (unsyncedCount > 0) {
                "You are offline — $unsyncedCount unsynced change(s). Will sync on reconnect."
            } else {
                "You are offline. Changes will sync when reconnected."
            },
            color = ArcaColors.Warning,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
        )
    }
}
