package com.app.fieldsync

import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val deviceId: String = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "Unknown-iOS-ID"
    override val deviceName: String = UIDevice.currentDevice.name
}

actual fun getPlatform(): Platform = IOSPlatform()