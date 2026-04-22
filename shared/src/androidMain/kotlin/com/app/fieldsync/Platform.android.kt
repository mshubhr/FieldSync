package com.app.fieldsync

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val deviceId: String = Build.ID
    override val deviceName: String = "${Build.MANUFACTURER} ${Build.MODEL}"
}

actual fun getPlatform(): Platform = AndroidPlatform()