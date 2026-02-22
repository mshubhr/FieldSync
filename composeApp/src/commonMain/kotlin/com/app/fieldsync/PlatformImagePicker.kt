package com.app.fieldsync

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformImagePicker(
    onImagePicked: (ByteArray?) -> Unit,
    onDismiss: () -> Unit
)