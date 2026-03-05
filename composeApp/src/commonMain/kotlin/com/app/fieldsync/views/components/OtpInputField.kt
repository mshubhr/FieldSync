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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(6) { index ->
            OutlinedTextField(
                value = otp.getOrNull(index)?.toString() ?: "",
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        val nextValue = newValue.lastOrNull()?.toString() ?: ""
                        val newOtp = otp.padEnd(6, ' ').toCharArray()
                        newOtp[index] = nextValue.firstOrNull() ?: ' '
                        onOtpChange(newOtp.concatToString().replace(" ", ""))

                        if (nextValue.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier.width(48.dp).focusRequester(focusRequesters[index])
                    .onKeyEvent { event ->
                        if (event.key == Key.Backspace && (otp.getOrNull(index) == null) && index > 0) {
                            focusRequesters[index - 1].requestFocus()
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