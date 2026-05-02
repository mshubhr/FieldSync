package com.app.fieldsync.views.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.app.fieldsync.views.screens.MainContent
import com.app.fieldsync.views.screens.OnboardingScreen
import com.app.fieldsync.views.screens.SignInScreen
import com.app.fieldsync.views.screens.SignUpScreen
import com.app.fieldsync.views.screens.SplashScreen
import com.russhwolf.settings.Settings

@Composable
@Preview
fun App() {
    val settings = remember { Settings() }
    val hasSeenSplash = remember { settings.getBoolean("has_seen_splash", false) }
    
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            var currentScreen by remember { 
                mutableStateOf(if (hasSeenSplash) Screen.SignIn else Screen.Splash)
            }

            when (currentScreen) {
                Screen.Splash -> SplashScreen(onSplashFinished = {
                    settings.putBoolean("has_seen_splash", true)
                    currentScreen = Screen.Onboarding
                })

                Screen.Onboarding -> OnboardingScreen(onOnboardingFinished = {
                    currentScreen = Screen.SignIn
                })

                Screen.SignIn -> SignInScreen(onSignInSuccess = {
                    currentScreen = Screen.Main
                }, onNavigateToSignUp = { currentScreen = Screen.SignUp })

                Screen.SignUp -> SignUpScreen(onSignUpSuccess = {
                    currentScreen = Screen.Main
                }, onNavigateToSignIn = { currentScreen = Screen.SignIn })

                Screen.Main -> MainContent()
            }
        }
    }
}
