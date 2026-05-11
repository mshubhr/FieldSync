package com.app.fieldsync.views.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@Composable
actual fun PlatformImagePicker(
    onImagePicked: (ByteArray?) -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = "image/*"

        input.onchange = {
            val file = input.files?.get(0)
            if (file != null) {
                val reader = FileReader()
                reader.onload = {
                    val result = reader.result
                    if (result is ArrayBuffer) {
                        val array = Uint8Array(result, 0, result.byteLength)
                        val bytes = ByteArray(array.length) { i -> array[i].toByte() }
                        onImagePicked(bytes)
                    } else {
                        onImagePicked(null)
                    }
                    onDismiss()
                }
                reader.readAsArrayBuffer(file)
            } else {
                onImagePicked(null)
                onDismiss()
            }
        }

        input.oncancel = {
            onDismiss()
        }

        input.click()
    }
}
