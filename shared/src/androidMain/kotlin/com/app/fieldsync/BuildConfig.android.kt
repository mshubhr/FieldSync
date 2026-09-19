package com.app.fieldsync

import android.os.Build

actual object BuildConfig {
    actual val baseUrl: String
        get() = if (
            Build.FINGERPRINT.startsWith("generic") ||
            Build.MODEL.contains("google_sdk") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu")
        ) {
            "http://10.0.2.2:8080"
        } else {
            "http://192.168.0.102:8080"
        }
}