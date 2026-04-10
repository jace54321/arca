package com.arca.android.ui.screens.vault

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arca.android.ui.components.ArcaPrimaryButton
import com.arca.android.ui.components.ArcaTopBar
import com.arca.android.ui.components.OfflineBanner
import com.arca.android.ui.screens.vault.components.AddEditSheet
import com.arca.android.ui.screens.vault.components.CredentialCard
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    onSyncLogsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: VaultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val filtered = viewModel.filteredCredentials

    Scaffold(
        topBar = {
            ArcaTopBar(
                syncStatus = uiState.syncStatus,
                onSyncLogsClick = onSyncLogsClick,
                onSettingsClick = onSettingsClick,
                onLogoutClick = onLogout,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddSheet() },
                containerColor = ArcaColors.Primary,
                contentColor = ArcaColors.OnPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add entry",
                    modifier = Modifier.size(28.dp),
                )
            }
        },
        containerColor = ArcaColors.Background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Offline banner
            if (!uiState.isOnline) {
                OfflineBanner(unsyncedCount = uiState.unsyncedCount)
            }

            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp),
                placeholder = {
                    Text(
                        "Search by name, username, or URL…",
                        color = ArcaColors.TextMuted,
                        fontFamily = UbuntuFontFamily,
                        fontSize = 14.sp,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = ArcaColors.TextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ArcaColors.TextPrimary,
                    unfocusedTextColor = ArcaColors.TextPrimary,
                    cursorColor = ArcaColors.Primary,
                    focusedBorderColor = ArcaColors.Primary,
                    unfocusedBorderColor = ArcaColors.Border,
                    focusedContainerColor = ArcaColors.Surface,
                    unfocusedContainerColor = ArcaColors.Surface,
                ),
                shape = RoundedCornerShape(10.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = UbuntuFontFamily,
                    fontSize = 14.sp,
                ),
            )

            // Credential list
            if (uiState.credentials.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = ArcaColors.Border,
                            modifier = Modifier.size(80.dp),
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Your vault is empty.",
                            color = ArcaColors.TextSecondary,
                            fontFamily = UbuntuFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Add your first password to get started.",
                            color = ArcaColors.TextMuted,
                            fontFamily = UbuntuFontFamily,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(24.dp))

                        ArcaPrimaryButton(
                            text = "Add Entry",
                            onClick = { viewModel.showAddSheet() },
                            icon = Icons.Filled.Add,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(
                        items = filtered,
                        key = { _, cred -> cred.id },
                    ) { index, credential ->
                        // Stagger animation (40ms per card, max 5)
                        val delay = (index.coerceAtMost(4) * 40).toLong()
                        var visible by remember { mutableStateOf(false) }

                        LaunchedEffect(credential.id) {
                            kotlinx.coroutines.delay(delay)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(200)),
                        ) {
                            CredentialCard(
                                credential = credential,
                                onClick = { viewModel.showEditSheet(credential) },
                            )
                        }
                    }

                    // Search no results
                    if (filtered.isEmpty() && uiState.searchQuery.isNotBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "No entries match '${uiState.searchQuery}'",
                                    color = ArcaColors.TextMuted,
                                    fontFamily = UbuntuFontFamily,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add/Edit bottom sheet
        if (uiState.showAddEditSheet) {
            AddEditSheet(
                editingCredential = uiState.editingCredential,
                onSave = { siteName, url, username, password, category, notes ->
                    val editing = uiState.editingCredential
                    if (editing != null) {
                        viewModel.updateCredential(editing.id, siteName, url, username, password, category, notes)
                    } else {
                        viewModel.addCredential(siteName, url, username, password, category, notes)
                    }
                },
                onDelete = uiState.editingCredential?.let { cred ->
                    { viewModel.deleteCredential(cred.id) }
                },
                onDismiss = { viewModel.hideSheet() },
            )
        }
    }
}
