package com.app.fieldsync.views.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.app.fieldsync.models.RamEntry
import com.app.fieldsync.reports.ReportRepository
import com.app.fieldsync.views.screens.MainContent
import com.app.fieldsync.views.screens.OnboardingScreen
import com.app.fieldsync.views.screens.ProfileScreen
import com.app.fieldsync.views.screens.SignInScreen
import com.app.fieldsync.views.screens.SignUpScreen
import com.app.fieldsync.views.screens.SplashScreen
import com.russhwolf.settings.Settings

@Composable
actual fun NavigationHost(
    initialScreen: Screen,
    reportRepository: ReportRepository,
    userName: String,
    historyEntries: List<RamEntry>,
    onLogout: () -> Unit,
    onReportSynced: (RamEntry) -> Unit,
    settings: Settings
) {
    var currentScreen by remember { mutableStateOf(initialScreen) }
    var user by remember { mutableStateOf(userName) }

    when (currentScreen) {
        Screen.Splash -> SplashScreen(onSplashFinished = {
            settings.putBoolean("has_seen_splash", true)
            currentScreen = Screen.Onboarding
        })
        Screen.Onboarding -> OnboardingScreen(onOnboardingFinished = {
            currentScreen = Screen.SignIn
        })
        Screen.SignIn -> SignInScreen(onSignInSuccess = { name ->
            user = name
            settings.putString("user_name", name)
            settings.putBoolean("is_logged_in", true)
            currentScreen = Screen.Main
        }, onNavigateToSignUp = { currentScreen = Screen.SignUp })
        Screen.SignUp -> SignUpScreen(onSignUpSuccess = { name ->
            user = name
            settings.putString("user_name", name)
            settings.putBoolean("is_logged_in", true)
            currentScreen = Screen.Main
        }, onNavigateToSignIn = { currentScreen = Screen.SignIn })
        Screen.Main -> MainContent(
            userName = user,
            historyEntries = historyEntries,
            reportRepository = reportRepository,
            onLogout = {
                onLogout()
                currentScreen = Screen.SignIn
            },
            onNavigateToProfile = {
                currentScreen = Screen.Profile
            },
            onReportSynced = onReportSynced
        )
        Screen.Profile -> ProfileScreen(
            userName = user,
            historyEntries = historyEntries,
            onBack = { currentScreen = Screen.Main }
        )
    }
}
