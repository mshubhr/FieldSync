package com.app.fieldsync

interface Platform {
    val name: String
    val deviceId: String
    val deviceName: String
}

expect fun getPlatform(): Platform