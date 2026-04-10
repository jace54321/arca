package com.arca.android.ui.screens.unlock

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arca.android.crypto.CryptoManager
import com.arca.android.ui.components.*
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

@Composable
fun UnlockScreen(
    email: String,
    derivedKeys: CryptoManager.DerivedKeys? = null,
    onUnlockSuccess: () -> Unit,
    viewModel: UnlockViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Auto-unlock if we have derived keys from login
    LaunchedEffect(derivedKeys) {
        if (derivedKeys != null) {
            viewModel.unlockWithKeys(derivedKeys)
        }
    }

    // Navigate on success
    LaunchedEffect(uiState.unlockSuccess) {
        if (uiState.unlockSuccess) {
            onUnlockSuccess()
        }
    }

    // Lock icon shake on error
    val shakeRotation by animateFloatAsState(
        targetValue = if (uiState.error != null) 1f else 0f,
        animationSpec = if (uiState.error != null) {
            keyframes {
                durationMillis = 500
                0f at 0
                -15f at 80
                15f at 160
                -10f at 240
                10f at 320
                0f at 500
            }
        } else {
            tween(0)
        },
        label = "lockShake"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcaColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Lock icon with red radial glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ArcaColors.Primary.copy(alpha = 0.15f),
                                    Color.Transparent,
                                ),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width / 2,
                            ),
                        )
                    },
            ) {
                Icon(
                    imageVector = if (uiState.unlockSuccess) Icons.Filled.LockOpen else Icons.Filled.Lock,
                    contentDescription = "Lock",
                    tint = ArcaColors.Primary,
                    modifier = Modifier
                        .size(72.dp)
                        .rotate(shakeRotation),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Wordmark
            Text(
                text = "Arca",
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = ArcaColors.TextPrimary,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Enter your Master Password to unlock.",
                fontFamily = UbuntuFontFamily,
                fontSize = 14.sp,
                color = ArcaColors.TextSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            // Password input
            PasswordField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Master Password",
                placeholder = "Enter your master password",
                isError = uiState.error != null,
                errorMessage = uiState.error,
                onImeAction = {
                    focusManager.clearFocus()
                    viewModel.unlockWithPassword(email)
                },
            )

            Spacer(Modifier.height(24.dp))

            // Unlock button
            ArcaPrimaryButton(
                text = "Unlock",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.unlockWithPassword(email)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState.isLoading,
                enabled = uiState.password.isNotBlank(),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "This is the only way to unlock your vault. It is never sent anywhere.",
                color = ArcaColors.TextMuted,
                fontFamily = UbuntuFontFamily,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}
