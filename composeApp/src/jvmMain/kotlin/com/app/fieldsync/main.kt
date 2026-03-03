package com.app.fieldsync

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.app.fieldsync.navigation.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FieldSync",
    ) {
        App()
    }
}