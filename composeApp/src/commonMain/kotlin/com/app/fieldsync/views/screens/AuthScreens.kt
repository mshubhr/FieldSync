package com.app.fieldsync.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.fieldsync.auth.AuthRepository
import com.app.fieldsync.views.components.OtpInputField
import com.app.fieldsync.views.components.PhoneInputField
import com.app.fieldsync.views.components.countries
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit, onNavigateToSignUp: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isPhoneValid = phoneNumber.length == selectedCountry.phoneLength

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to continue to FieldSync",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            PhoneInputField(
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { phoneNumber = it },
                selectedCountry = selectedCountry,
                onCountrySelected = {
                    selectedCountry = it
                    phoneNumber = ""
                },
                enabled = !isOtpSent && !isLoading
            )

            if (isOtpSent) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Enter 6-digit OTP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OtpInputField(
                    otp = otp, onOtpChange = { if (it.length <= 6) otp = it }, enabled = !isLoading
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!isOtpSent) {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            val result = repository.sendOtp(selectedCountry.code + phoneNumber)
                            if (result.isSuccess) {
                                isOtpSent = true
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to send OTP"
                            }
                            isLoading = false
                        }
                    } else {
                        onSignInSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading && (if (!isOtpSent) isPhoneValid else otp.length == 6)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (isOtpSent) "Verify & Sign In" else "Send OTP")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit, onNavigateToSignIn: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var isOtpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isPhoneValid = phoneNumber.length == selectedCountry.phoneLength

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { newValue ->
                    if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                        fullName = newValue
                    }
                },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isOtpSent && !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            PhoneInputField(
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { phoneNumber = it },
                selectedCountry = selectedCountry,
                onCountrySelected = {
                    selectedCountry = it
                    phoneNumber = ""
                },
                enabled = !isOtpSent && !isLoading
            )

            if (isOtpSent) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Enter 6-digit OTP",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OtpInputField(
                    otp = otp, onOtpChange = { if (it.length <= 6) otp = it }, enabled = !isLoading
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!isOtpSent) {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            val fullPhone = selectedCountry.code + phoneNumber
                            val result = repository.sendOtp(fullPhone)
                            if (result.isSuccess) {
                                isOtpSent = true
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to send OTP"
                            }
                            isLoading = false
                        }
                    } else {
                        onSignUpSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading && (if (!isOtpSent) (isPhoneValid && fullName.isNotBlank()) else otp.length == 6)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (isOtpSent) "Create Account" else "Send OTP")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignIn) {
                Text("Already have an account? Sign In")
            }
        }
    }
}

@Preview
@Composable
fun SignInScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        SignInScreen(onSignInSuccess = {}, onNavigateToSignUp = {})
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        SignUpScreen(onSignUpSuccess = {}, onNavigateToSignIn = {})
    }
}
