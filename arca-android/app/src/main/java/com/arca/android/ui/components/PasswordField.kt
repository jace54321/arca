package com.arca.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.JetBrainsMonoFontFamily
import com.arca.android.ui.theme.UbuntuFontFamily

/**
 * Password field with eye toggle. Uses JetBrains Mono when revealed.
 * Matches the design brief's 52px height with red accent glow on focus.
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    helperText: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true,
) {
    var isVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Label
        Text(
            text = label.uppercase(),
            color = if (isError) ArcaColors.Error else ArcaColors.TextSecondary,
            fontFamily = UbuntuFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            letterSpacing = 0.04.sp,
        )

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .onFocusChanged { isFocused = it.isFocused }
                .then(
                    if (isFocused && !isError) {
                        Modifier.drawBehind {
                            drawRoundRect(
                                color = ArcaColors.PrimaryGlow,
                                style = Stroke(width = 6.dp.toPx()),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                            )
                        }
                    } else Modifier
                ),
            enabled = enabled,
            singleLine = true,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = ArcaColors.TextMuted,
                    fontFamily = UbuntuFontFamily,
                    fontSize = 14.sp,
                )
            },
            trailingIcon = {
                IconButton(onClick = { isVisible = !isVisible }) {
                    Icon(
                        imageVector = if (isVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (isVisible) "Hide password" else "Show password",
                        tint = if (isFocused) ArcaColors.Primary else ArcaColors.TextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() },
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ArcaColors.TextPrimary,
                unfocusedTextColor = ArcaColors.TextPrimary,
                cursorColor = ArcaColors.Primary,
                focusedBorderColor = ArcaColors.Primary,
                unfocusedBorderColor = ArcaColors.Border,
                errorBorderColor = ArcaColors.Error,
                focusedContainerColor = ArcaColors.Surface,
                unfocusedContainerColor = ArcaColors.Surface,
            ),
            shape = RoundedCornerShape(6.dp),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = if (isVisible) JetBrainsMonoFontFamily else UbuntuFontFamily,
                fontSize = 14.sp,
            ),
        )

        // Error / Helper text
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = ArcaColors.Error,
                fontFamily = UbuntuFontFamily,
                fontSize = 12.sp,
            )
        } else if (helperText != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = helperText,
                color = ArcaColors.TextMuted,
                fontFamily = UbuntuFontFamily,
                fontSize = 12.sp,
            )
        }
    }
}
