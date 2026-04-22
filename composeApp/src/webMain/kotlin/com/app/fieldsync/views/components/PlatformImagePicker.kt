package com.app.fieldsync.views.components

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformImagePicker(
    onImagePicked: (ByteArray?) -> Unit,
    onDismiss: () -> Unit
) {
    onDismiss()
}