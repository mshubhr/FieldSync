package com.app.fieldsync

class JsPlatform : Platform {
    override val name: String = "JavaScript"
}

actual fun getPlatform(): Platform = JsPlatform()
