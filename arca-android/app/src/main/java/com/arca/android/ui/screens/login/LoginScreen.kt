package com.arca.android.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arca.android.ui.components.*
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Navigate on success
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    // Shake animation on error
    val shakeOffset by animateFloatAsState(
        targetValue = if (uiState.error != null) 1f else 0f,
        animationSpec = if (uiState.error != null) {
            keyframes {
                durationMillis = 400
                0f at 0
                -8f at 50
                8f at 100
                -8f at 150
                8f at 200
                -4f at 250
                4f at 300
                0f at 400
            }
        } else {
            tween(0)
        },
        label = "shakeAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcaColors.Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(80.dp))

            // ── Logo + Tagline ──
            Text(
                text = "Arca",
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = ArcaColors.TextPrimary,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your vault. Your key.",
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Light,
                fontSize = 18.sp,
                color = ArcaColors.TextSecondary,
            )

            Spacer(Modifier.height(24.dp))

            // ── Trust pillars ──
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.widthIn(max = 300.dp),
            ) {
                TrustPillar(Icons.Outlined.Lock, "Zero-knowledge architecture")
                TrustPillar(Icons.Outlined.PhoneAndroid, "Works offline, syncs silently")
                TrustPillar(Icons.Outlined.Key, "Only you hold the key")
            }

            Spacer(Modifier.height(40.dp))

            // ── Tab switcher ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArcaColors.Surface, RoundedCornerShape(9999.dp))
                    .padding(4.dp),
            ) {
                TabButton(
                    text = "Log In",
                    isSelected = uiState.isLoginMode,
                    onClick = { viewModel.setLoginMode(true) },
                    modifier = Modifier.weight(1f),
                )

                TabButton(
                    text = "Create Account",
                    isSelected = !uiState.isLoginMode,
                    onClick = { viewModel.setLoginMode(false) },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Form fields ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = shakeOffset.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ArcaTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = "Email Address",
                    placeholder = "you@example.com",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                )

                PasswordField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = "Master Password",
                    placeholder = "Enter your master password",
                    isError = uiState.error != null,
                    imeAction = if (uiState.isLoginMode) {
                        androidx.compose.ui.text.input.ImeAction.Done
                    } else {
                        androidx.compose.ui.text.input.ImeAction.Next
                    },
                    onImeAction = {
                        if (uiState.isLoginMode) {
                            focusManager.clearFocus()
                            viewModel.submit()
                        }
                    },
                )

                // Register-only fields
                AnimatedVisibility(
                    visible = !uiState.isLoginMode,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        PasswordStrengthMeter(password = uiState.password)

                        PasswordField(
                            value = uiState.confirmPassword,
                            onValueChange = { viewModel.updateConfirmPassword(it) },
                            label = "Confirm Master Password",
                            placeholder = "Re-enter your master password",
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setAgreedToTerms(!uiState.agreedToTerms) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = uiState.agreedToTerms,
                                onCheckedChange = { viewModel.setAgreedToTerms(it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = ArcaColors.Primary,
                                    uncheckedColor = ArcaColors.Border,
                                    checkmarkColor = ArcaColors.OnPrimary,
                                ),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "I understand my Master Password cannot be recovered. I will keep it safe.",
                                color = ArcaColors.TextSecondary,
                                fontFamily = UbuntuFontFamily,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }

                // Error message
                AnimatedVisibility(visible = uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = ArcaColors.Error,
                        fontFamily = UbuntuFontFamily,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Submit button
                ArcaPrimaryButton(
                    text = if (uiState.isLoginMode) "Log In" else "Create Account",
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.submit()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isLoading,
                    enabled = if (uiState.isLoginMode) {
                        uiState.email.isNotBlank() && uiState.password.isNotBlank()
                    } else {
                        uiState.email.isNotBlank() && uiState.password.isNotBlank() &&
                            uiState.confirmPassword.isNotBlank() && uiState.agreedToTerms
                    },
                )

                // Helper text
                Text(
                    text = if (uiState.isLoginMode) {
                        "Forgot your Master Password? Unfortunately, we can't help — it's never sent to us."
                    } else {
                        "Your Master Password encrypts your vault. We never see it, never store it, and cannot recover it."
                    },
                    color = ArcaColors.TextMuted,
                    fontFamily = UbuntuFontFamily,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TrustPillar(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ArcaColors.Primary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = ArcaColors.TextSecondary,
            fontFamily = UbuntuFontFamily,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(9999.dp),
        color = if (isSelected) ArcaColors.Primary else Color.Transparent,
        contentColor = if (isSelected) ArcaColors.OnPrimary else ArcaColors.TextMuted,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
        }
    }
}
