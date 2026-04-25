package com.arca.android.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arca.android.ui.theme.ArcaColors
import com.arca.android.ui.theme.UbuntuFontFamily

/**
 * Standard Arca text field with label, focus glow, and error support.
 */
@Composable
fun ArcaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    helperText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
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

        // Input field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (singleLine) 52.dp else 96.dp)
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
            singleLine = singleLine,
            maxLines = maxLines,
            placeholder = {
                Text(
                    text = placeholder,
                    color = ArcaColors.TextMuted,
                    fontFamily = UbuntuFontFamily,
                    fontSize = 14.sp,
                )
            },
            trailingIcon = trailingIcon,
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() },
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
                disabledContainerColor = ArcaColors.Surface.copy(alpha = 0.5f),
            ),
            shape = RoundedCornerShape(6.dp),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = UbuntuFontFamily,
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
