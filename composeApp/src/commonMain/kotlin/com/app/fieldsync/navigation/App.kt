package com.app.fieldsync.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.app.fieldsync.screens.OnboardingScreen
import com.app.fieldsync.screens.SplashScreen
import com.app.fieldsync.screens.MainContent
import com.app.fieldsync.screens.ReorderableListScreen
import com.app.fieldsync.screens.SignInScreen
import com.app.fieldsync.screens.SignUpScreen

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

        when (currentScreen) {
            Screen.Splash -> SplashScreen(onSplashFinished = { 
                currentScreen = Screen.Onboarding 
            })
            
            Screen.Onboarding -> OnboardingScreen(onOnboardingFinished = {
                currentScreen = Screen.SignIn
            })

            Screen.SignIn -> SignInScreen(
                onSignInSuccess = { currentScreen = Screen.ReorderableList },
                onNavigateToSignUp = { currentScreen = Screen.SignUp },
                onBack = { currentScreen = Screen.Onboarding }
            )

            Screen.SignUp -> SignUpScreen(
                onSignUpSuccess = { currentScreen = Screen.ReorderableList },
                onNavigateToSignIn = { currentScreen = Screen.SignIn },
                onBack = { currentScreen = Screen.SignIn }
            )

            Screen.ReorderableList -> ReorderableListScreen()

            Screen.Main -> MainContent()
        }
    }
}
