package com.arca.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arca.android.ui.components.*
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily
import com.arca.android.util.toDisplayTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle logout
    LaunchedEffect(uiState.logoutTriggered) {
        if (uiState.logoutTriggered) onLogout()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontFamily = UbuntuFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ArcaColors.TextPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ArcaColors.TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArcaColors.Background,
                ),
            )
        },
        containerColor = ArcaColors.Background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Profile Section ──
            item {
                SectionHeader("Profile")
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ArcaColors.Surface),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArcaColors.Border),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Avatar placeholder
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ArcaColors.Primary.copy(alpha = 0.15f)),
                            ) {
                                Text(
                                    text = uiState.username.firstOrNull()?.uppercase()
                                        ?: uiState.email.firstOrNull()?.uppercase()
                                        ?: "?",
                                    color = ArcaColors.Primary,
                                    fontFamily = UbuntuFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = uiState.email,
                                    color = ArcaColors.TextSecondary,
                                    fontFamily = UbuntuFontFamily,
                                    fontSize = 13.sp,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        ArcaTextField(
                            value = uiState.username,
                            onValueChange = { viewModel.updateUsername(it) },
                            label = "Username",
                            placeholder = "Your display name",
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            ArcaPrimaryButton(
                                text = if (uiState.saveSuccess) "Saved ✓" else "Save",
                                onClick = { viewModel.saveProfile() },
                                isLoading = uiState.isSaving,
                                enabled = uiState.username.isNotBlank(),
                            )
                        }
                    }
                }
            }

            // ── Devices Section ──
            item {
                SectionHeader("Devices")
            }

            if (uiState.isLoadingDevices) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = ArcaColors.Primary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                }
            } else {
                items(uiState.devices, key = { it.id }) { device ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ArcaColors.Surface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ArcaColors.Border),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = if (device.deviceType == "mobile") {
                                    Icons.Outlined.PhoneAndroid
                                } else {
                                    Icons.Outlined.DesktopWindows
                                },
                                contentDescription = null,
                                tint = ArcaColors.TextSecondary,
                                modifier = Modifier.size(24.dp),
                            )

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.deviceName,
                                    color = ArcaColors.TextPrimary,
                                    fontFamily = UbuntuFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    text = "Last active: ${device.lastActive.toDisplayTimestamp()}",
                                    color = ArcaColors.TextMuted,
                                    fontFamily = UbuntuFontFamily,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }

            // ── About Section ──
            item {
                SectionHeader("About")
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ArcaColors.Surface),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArcaColors.Border),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow("App Version", "1.0.0")
                        InfoRow("Architecture", "Zero-Knowledge")
                        InfoRow("Encryption", "AES-256-GCM")
                        InfoRow("Key Derivation", "PBKDF2-SHA256 (600k)")
                    }
                }
            }

            // ── Logout ──
            item {
                Spacer(Modifier.height(8.dp))

                ArcaDangerButton(
                    text = "Logout",
                    onClick = { viewModel.logout() },
                    icon = Icons.Outlined.Logout,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = ArcaColors.TextSecondary,
        fontFamily = UbuntuFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.08.sp,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = ArcaColors.TextSecondary,
            fontFamily = UbuntuFontFamily,
            fontSize = 14.sp,
        )
        Text(
            text = value,
            color = ArcaColors.TextPrimary,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}
