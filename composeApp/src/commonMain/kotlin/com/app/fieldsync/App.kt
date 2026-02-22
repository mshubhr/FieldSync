package com.app.fieldsync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showImagePicker by remember { mutableStateOf(false) }
        var imageStatus by remember { mutableStateOf("No image selected") }

        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding().fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = imageStatus)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                showImagePicker = true
            }) {
                Text("Add an image")
            }

            if (showImagePicker) {
                PlatformImagePicker(onImagePicked = { bytes ->
                    imageStatus = if (bytes != null) {
                        "Image selected (${bytes.size} bytes)"
                    } else {
                        "Selection cancelled"
                    }
                    showImagePicker = false
                }, onDismiss = {
                    showImagePicker = false
                })
            }
        }
    }
}