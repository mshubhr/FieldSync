package com.app.fieldsync

class WasmJsPlatform : Platform {
    override val name: String = "Web (Wasm)"
    override val deviceId: String = "WebBrowserWasm"
    override val deviceName: String = "Browser"
}

actual fun getPlatform(): Platform = WasmJsPlatform()
