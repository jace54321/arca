package com.arca.android.ui.screens.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arca.android.data.api.dto.SyncLogDTO
import com.arca.android.ui.components.SyncStatusBadge
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.MonoStyles
import com.arca.android.ui.theme.UbuntuFontFamily
import com.arca.android.util.toDisplayTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncLogsScreen(
    onBack: () -> Unit,
    viewModel: SyncLogsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sync History",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "A complete record of every time your vault was synchronized.",
                color = ArcaColors.TextSecondary,
                fontFamily = UbuntuFontFamily,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = ArcaColors.Primary)
                    }
                }

                uiState.logs.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No sync events yet.\nYour vault has never been synchronized.",
                            color = ArcaColors.TextMuted,
                            fontFamily = UbuntuFontFamily,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.logs, key = { it.id }) { log ->
                            SyncLogCard(log = log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncLogCard(log: SyncLogDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcaColors.Surface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArcaColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Current device indicator dot
            if (log.isCurrentDevice == true) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ArcaColors.Primary)
                        .offset(y = 6.dp),
                )
                Spacer(Modifier.width(8.dp))
            }

            // Device icon
            Icon(
                imageVector = if (log.deviceType == "mobile") {
                    Icons.Outlined.PhoneAndroid
                } else {
                    Icons.Outlined.DesktopWindows
                },
                contentDescription = null,
                tint = ArcaColors.TextSecondary,
                modifier = Modifier.size(20.dp),
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = log.device,
                        color = ArcaColors.TextPrimary,
                        fontFamily = UbuntuFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )

                    SyncStatusBadge(status = log.status, compact = false)
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = log.timestamp.toDisplayTimestamp(),
                        color = ArcaColors.TextMuted,
                        fontFamily = UbuntuFontFamily,
                        fontSize = 12.sp,
                    )

                    if (log.versionFrom != null && log.versionTo != null) {
                        Text(
                            text = "v${log.versionFrom} → v${log.versionTo}",
                            style = MonoStyles.monoSm,
                            color = ArcaColors.TextMuted,
                        )
                    }
                }

                if (!log.message.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = log.message,
                        color = if (log.status == "error") ArcaColors.Error else ArcaColors.TextMuted,
                        fontFamily = UbuntuFontFamily,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}
