package com.app.fieldsync

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformImagePicker(
    onImagePicked: (ByteArray?) -> Unit,
    onDismiss: () -> Unit
) {
    onDismiss()
}