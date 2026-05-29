package com.app.fieldsync.views.navigation

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object SignIn : Screen()
    object SignUp : Screen()
    object Main : Screen()
    object Profile : Screen()
}
