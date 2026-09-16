package com.app.fieldsync.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OtpInputField(
    otp: String, onOtpChange: (String) -> Unit, enabled: Boolean = true
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    val targetFocusIndex = otp.length.coerceAtMost(5)
    LaunchedEffect(targetFocusIndex) {
        if (enabled && otp.length < 6) {
            focusRequesters[targetFocusIndex].requestFocus()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(6) { index ->
            val charStr = otp.getOrNull(index)?.toString() ?: ""
            OutlinedTextField(
                value = charStr,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    if (digits.isNotEmpty()) {
                        if (digits.length >= 6) {
                            onOtpChange(digits.take(6))
                        } else {
                            val nextValue = digits.lastOrNull()?.toString() ?: ""
                            val newOtp = otp.padEnd(6, ' ').toCharArray()
                            newOtp[index] = nextValue.firstOrNull() ?: ' '
                            onOtpChange(newOtp.concatToString().trimEnd().replace(" ", ""))
                        }
                    } else if (newValue.isEmpty()) {
                        val newOtp = otp.padEnd(6, ' ').toCharArray()
                        newOtp[index] = ' '
                        onOtpChange(newOtp.concatToString().trimEnd().replace(" ", ""))
                    }
                },
                modifier = Modifier.width(48.dp).focusRequester(focusRequesters[index])
                    .onKeyEvent { event ->
                        if (event.key == Key.Backspace && charStr.isEmpty() && index > 0) {
                            val nextOtp = if (otp.length > index - 1) otp.take(index - 1) else otp
                            onOtpChange(nextOtp)
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = enabled,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}