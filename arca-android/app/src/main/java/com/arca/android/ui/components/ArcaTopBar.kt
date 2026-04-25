package com.arca.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

/**
 * Arca top app bar — "Arca" wordmark + sync status + overflow menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcaTopBar(
    syncStatus: String = "synced",
    onSyncLogsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Arca",
                    fontFamily = UbuntuFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = ArcaColors.TextPrimary,
                )

                SyncStatusBadge(status = syncStatus, compact = true)
            }
        },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menu",
                        tint = ArcaColors.TextSecondary,
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = ArcaColors.SurfaceRaised,
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Sync Logs",
                                color = ArcaColors.TextPrimary,
                                fontFamily = UbuntuFontFamily,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onSyncLogsClick()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Settings",
                                color = ArcaColors.TextPrimary,
                                fontFamily = UbuntuFontFamily,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onSettingsClick()
                        },
                    )
                    HorizontalDivider(color = ArcaColors.Border, thickness = 0.5.dp)
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Logout",
                                color = ArcaColors.Error,
                                fontFamily = UbuntuFontFamily,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onLogoutClick()
                        },
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArcaColors.Background,
            titleContentColor = ArcaColors.TextPrimary,
        ),
        modifier = modifier,
    )
}
