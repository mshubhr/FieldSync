package com.app.fieldsync.views.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable data object Splash : Screen
    @Serializable data object Onboarding : Screen
    @Serializable data object SignIn : Screen
    @Serializable data object SignUp : Screen
    @Serializable data object Main : Screen
    @Serializable data object Profile : Screen
}
