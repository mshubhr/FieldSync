package com.app.fieldsync

class JvmPlatform : Platform {
    override val name: String = "JVM"
    override val deviceId: String = System.getProperty("user.name") ?: "Unknown-JVM-ID"
    override val deviceName: String = System.getProperty("os.name") ?: "Unknown-JVM-Device"
}

actual fun getPlatform(): Platform = JvmPlatform()
