package com.arca.android.ui.screens.vault.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.data.repository.Credential
import com.arca.android.ui.components.SyncStatusBadge
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.JetBrainsMonoFontFamily
import com.arca.android.ui.theme.MonoStyles
import com.arca.android.ui.theme.UbuntuFontFamily
import com.arca.android.util.maskUsername

/**
 * Credential card — the core repeating element in the vault list.
 */
@Composable
fun CredentialCard(
    credential: Credential,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = ArcaColors.Surface,
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArcaColors.Border),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // ── Top row: Favicon + Site name ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Favicon placeholder (first letter circle)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ArcaColors.Primary.copy(alpha = 0.15f)),
                ) {
                    Text(
                        text = credential.siteName.firstOrNull()?.uppercase() ?: "?",
                        color = ArcaColors.Primary,
                        fontFamily = UbuntuFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = credential.siteName,
                        color = ArcaColors.TextPrimary,
                        fontFamily = UbuntuFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (credential.url.isNotBlank()) {
                        Text(
                            text = credential.url,
                            color = ArcaColors.TextMuted,
                            fontFamily = UbuntuFontFamily,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Username ──
            Text(
                text = credential.username.maskUsername(),
                style = MonoStyles.monoMd,
                color = ArcaColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(8.dp))

            // ── Password row ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (isPasswordVisible) credential.password else "••••••••••",
                    style = MonoStyles.monoMd,
                    color = if (isPasswordVisible) ArcaColors.TextPrimary else ArcaColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                // Eye toggle
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide" else "Show",
                        tint = ArcaColors.TextSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }

                // Copy button
                IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(credential.password)) },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy password",
                        tint = ArcaColors.TextSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            // ── Sync status strip ──
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                SyncStatusBadge(status = credential.syncStatus, compact = true)
            }
        }
    }
}
