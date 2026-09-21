package com.app.fieldsync.views.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.app.fieldsync.models.RamEntry
import com.app.fieldsync.reports.ReportRepository
import com.app.fieldsync.views.screens.MainContent
import com.app.fieldsync.views.screens.OnboardingScreen
import com.app.fieldsync.views.screens.ProfileScreen
import com.app.fieldsync.views.screens.SignInScreen
import com.app.fieldsync.views.screens.SignUpScreen
import com.app.fieldsync.views.screens.SplashScreen
import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable

@Serializable
data object SplashKey : NavKey

@Serializable
data object OnboardingKey : NavKey

@Serializable
data object SignInKey : NavKey

@Serializable
data object SignUpKey : NavKey

@Serializable
data object MainKey : NavKey

@Serializable
data object ProfileKey : NavKey

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
    var user by remember { mutableStateOf(userName) }

    val initialKey = when (initialScreen) {
        Screen.Splash -> SplashKey
        Screen.Onboarding -> OnboardingKey
        Screen.SignIn -> SignInKey
        Screen.SignUp -> SignUpKey
        Screen.Main -> MainKey
        Screen.Profile -> ProfileKey
    }

    val backStack = rememberNavBackStack(initialKey)
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo).copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)
    val isProfileOpen = backStack.lastOrNull() == ProfileKey

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider = entryProvider {
            entry<SplashKey> {
                SplashScreen(onSplashFinished = {
                    settings.putBoolean("has_seen_splash", true)
                    while (backStack.isNotEmpty()) backStack.removeLastOrNull()
                    backStack.add(OnboardingKey)
                })
            }
            entry<OnboardingKey> {
                OnboardingScreen(onOnboardingFinished = {
                    while (backStack.isNotEmpty()) backStack.removeLastOrNull()
                    backStack.add(SignInKey)
                })
            }
            entry<SignInKey> {
                SignInScreen(onSignInSuccess = { name ->
                    user = name
                    settings.putString("user_name", name)
                    settings.putBoolean("is_logged_in", true)
                    while (backStack.isNotEmpty()) backStack.removeLastOrNull()
                    backStack.add(MainKey)
                }, onNavigateToSignUp = {
                    backStack.add(SignUpKey)
                })
            }
            entry<SignUpKey> {
                SignUpScreen(onSignUpSuccess = { name ->
                    user = name
                    settings.putString("user_name", name)
                    settings.putBoolean("is_logged_in", true)
                    while (backStack.isNotEmpty()) backStack.removeLastOrNull()
                    backStack.add(MainKey)
                }, onNavigateToSignIn = {
                    backStack.removeLastOrNull()
                })
            }
            entry<MainKey>(
                metadata = if (isProfileOpen) {
                    ListDetailSceneStrategy.listPane()
                } else {
                    emptyMap()
                }
            ) {
                MainContent(
                    userName = user,
                    historyEntries = historyEntries,
                    reportRepository = reportRepository,
                    onLogout = {
                        onLogout()
                        while (backStack.isNotEmpty()) backStack.removeLastOrNull()
                        backStack.add(SignInKey)
                    },
                    onNavigateToProfile = {
                        if (!backStack.contains(ProfileKey)) backStack.add(ProfileKey)
                    },
                    onReportSynced = onReportSynced
                )
            }
            entry<ProfileKey>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                ProfileScreen(
                    userName = user, historyEntries = historyEntries, onBack = {
                        backStack.removeLastOrNull()
                    })
            }
        })
}