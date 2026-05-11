package com.app.fieldsync

import kotlinx.browser.window

class JsPlatform : Platform {
    override val name: String = "JavaScript"
    override val deviceId: String = window.navigator.userAgent
    override val deviceName: String = window.navigator.appName
}

actual fun getPlatform(): Platform = JsPlatform()
