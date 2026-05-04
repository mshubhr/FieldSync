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
import com.app.fieldsync.db.DatabaseDriverFactory
import com.app.fieldsync.db.FieldSyncDatabase
import com.app.fieldsync.reports.LocalReportDataSource
import com.app.fieldsync.reports.ReportRepository
import com.app.fieldsync.views.screens.MainContent
import com.app.fieldsync.views.screens.OnboardingScreen
import com.app.fieldsync.views.screens.SignInScreen
import com.app.fieldsync.views.screens.SignUpScreen
import com.app.fieldsync.views.screens.SplashScreen
import com.russhwolf.settings.Settings

@Composable
@Preview
fun App(databaseDriverFactory: DatabaseDriverFactory? = null) {
    val settings = remember { Settings() }
    val hasSeenSplash = remember { settings.getBoolean("has_seen_splash", false) }
    val isLoggedIn = remember { settings.getBoolean("is_logged_in", false) }
    var userName by remember { mutableStateOf(settings.getString("user_name", "User")) }

    val reportRepository = remember {
        val driver = databaseDriverFactory?.createDriver()
        val localDataSource = driver?.let { LocalReportDataSource(FieldSyncDatabase(it)) }
        ReportRepository(localDataSource)
    }

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
                mutableStateOf(
                    when {
                        !hasSeenSplash -> Screen.Splash
                        isLoggedIn -> Screen.Main
                        else -> Screen.SignIn
                    }
                )
            }

            when (currentScreen) {
                Screen.Splash -> SplashScreen(onSplashFinished = {
                    settings.putBoolean("has_seen_splash", true)
                    currentScreen = Screen.Onboarding
                })

                Screen.Onboarding -> OnboardingScreen(onOnboardingFinished = {
                    currentScreen = Screen.SignIn
                })

                Screen.SignIn -> SignInScreen(onSignInSuccess = { name ->
                    userName = name
                    settings.putString("user_name", name)
                    settings.putBoolean("is_logged_in", true)
                    currentScreen = Screen.Main
                }, onNavigateToSignUp = { currentScreen = Screen.SignUp })

                Screen.SignUp -> SignUpScreen(onSignUpSuccess = { name ->
                    userName = name
                    settings.putString("user_name", name)
                    settings.putBoolean("is_logged_in", true)
                    currentScreen = Screen.Main
                }, onNavigateToSignIn = { currentScreen = Screen.SignIn })

                Screen.Main -> MainContent(
                    userName = userName, reportRepository = reportRepository, onLogout = {
                        settings.putBoolean("is_logged_in", false)
                        currentScreen = Screen.SignIn
                    })
            }
        }
    }
}
