package com.app.fieldsync.views.components

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformImagePicker(
    onImagePicked: (ByteArray?) -> Unit,
    onDismiss: () -> Unit
)