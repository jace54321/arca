package com.arca.android.ui.screens.vault.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.data.repository.Credential
import com.arca.android.ui.components.*
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily
import com.arca.android.util.PasswordGenerator

/**
 * Bottom sheet for adding or editing a credential entry.
 * Matches the design brief's mobile add/edit spec.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSheet(
    editingCredential: Credential?,
    onSave: (String, String?, String, String, String, String?) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    val isEdit = editingCredential != null

    var siteName by remember(editingCredential) { mutableStateOf(editingCredential?.siteName ?: "") }
    var url by remember(editingCredential) { mutableStateOf(editingCredential?.url ?: "") }
    var username by remember(editingCredential) { mutableStateOf(editingCredential?.username ?: "") }
    var password by remember(editingCredential) { mutableStateOf(editingCredential?.password ?: "") }
    var category by remember(editingCredential) { mutableStateOf(editingCredential?.category ?: "Other") }
    var notes by remember(editingCredential) { mutableStateOf(editingCredential?.notes ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    val categories = listOf("Work", "Personal", "Social", "Other")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ArcaColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .padding(0.dp),
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(2.dp),
                    color = ArcaColors.Border,
                ) {}
            }
        },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
                .imePadding(),
        ) {
            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isEdit) "Edit Entry" else "New Entry",
                    fontFamily = UbuntuFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    color = ArcaColors.TextPrimary,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = ArcaColors.TextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Fields ──
            ArcaTextField(
                value = siteName,
                onValueChange = { siteName = it },
                label = "Site Name",
                placeholder = "e.g. GitHub, Google, Netflix",
            )

            Spacer(Modifier.height(16.dp))

            ArcaTextField(
                value = url,
                onValueChange = { url = it },
                label = "URL",
                placeholder = "https://…",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri,
            )

            Spacer(Modifier.height(16.dp))

            ArcaTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username / Email",
                placeholder = "your@email.com",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            )

            Spacer(Modifier.height(16.dp))

            PasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter password",
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
            )

            Spacer(Modifier.height(8.dp))

            // Generate password button
            TextButton(
                onClick = { password = PasswordGenerator.generate() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ArcaColors.Primary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Casino,
                    contentDescription = "Generate",
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Generate Strong Password",
                    fontFamily = UbuntuFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            PasswordStrengthMeter(password = password)

            Spacer(Modifier.height(16.dp))

            // Category chips (horizontal scroll)
            Text(
                text = "CATEGORY",
                color = ArcaColors.TextSecondary,
                fontFamily = UbuntuFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                letterSpacing = 0.04.sp,
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = {
                            Text(
                                text = cat,
                                fontFamily = UbuntuFontFamily,
                                fontSize = 13.sp,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ArcaColors.Primary.copy(alpha = 0.15f),
                            selectedLabelColor = ArcaColors.Primary,
                            containerColor = ArcaColors.SurfaceRaised,
                            labelColor = ArcaColors.TextSecondary,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = ArcaColors.Border,
                            selectedBorderColor = ArcaColors.Primary,
                            enabled = true,
                            selected = category == cat,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            ArcaTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                placeholder = "Optional notes…",
                singleLine = false,
                maxLines = 3,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
            )

            Spacer(Modifier.height(24.dp))

            // ── Action buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isEdit && onDelete != null) {
                    ArcaDangerButton(
                        text = "Delete",
                        onClick = onDelete,
                    )
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                ArcaPrimaryButton(
                    text = "Save Entry",
                    onClick = {
                        isSaving = true
                        onSave(
                            siteName,
                            url.ifBlank { null },
                            username,
                            password,
                            category,
                            notes.ifBlank { null },
                        )
                    },
                    isLoading = isSaving,
                    enabled = siteName.isNotBlank() && username.isNotBlank() && password.isNotBlank(),
                )
            }
        }
    }
}
