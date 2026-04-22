package com.app.fieldsync.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fieldsync.views.components.PlatformImagePicker
import com.app.fieldsync.views.components.DocumentTypeDropdown
import com.app.fieldsync.views.components.HistoryChart
import com.app.fieldsync.models.RamEntry
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    var showImagePicker by remember { mutableStateOf(false) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedDocType by remember { mutableStateOf("") }
    var isSyncing by remember { mutableStateOf(false) }
    
    var historyEntries by remember { mutableStateOf(emptyList<RamEntry>()) }
    
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var chartPosition by remember { mutableStateOf(Offset.Zero) }
    var chartSize by remember { mutableStateOf(Offset.Zero) }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "FieldSync",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black, titleContentColor = Color.White
                )
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.05f), Color.White)
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Data Collection",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Complete the fields below to sync data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

//                DocumentTypeDropdown(selectedType = selectedDocType, onTypeSelected = { selectedDocType = it })

                Card(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (imageBytes != null) MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = 0.3f
                        )
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    onClick = { if (!isDragging) showImagePicker = true }) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = imageBytes, label = "ImageStatus"
                        ) { currentImageBytes ->
                            if (currentImageBytes != null) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape,
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Document Captured",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    val sizeKb = currentImageBytes.size / 1024
                                    
                                    Box {
                                        Text(
                                            text = "$sizeKb KB • Drag to Chart",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDragging) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                                .pointerInput(Unit) {
                                                    detectDragGestures(
                                                        onDragStart = { isDragging = true },
                                                        onDragEnd = {
                                                            isDragging = false
                                                            if (offsetY > 200f) {
                                                                val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                                                                historyEntries = historyEntries + RamEntry(sizeKb, today)
                                                            }
                                                            offsetX = 0f
                                                            offsetY = 0f
                                                        },
                                                        onDragCancel = {
                                                            isDragging = false
                                                            offsetX = 0f
                                                            offsetY = 0f
                                                        },
                                                        onDrag = { change, dragAmount ->
                                                            change.consume()
                                                            offsetX += dragAmount.x
                                                            offsetY += dragAmount.y
                                                        }
                                                    )
                                                }
                                                .background(
                                                    if (isDragging) Color.Black.copy(alpha = 0.1f) else Color.Transparent,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Tap to take a photo",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                HistoryChart(
                    entries = historyEntries,
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            chartPosition = layoutCoordinates.positionInRoot()
                            chartSize = Offset(layoutCoordinates.size.width.toFloat(), layoutCoordinates.size.height.toFloat())
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isReady = imageBytes != null && selectedDocType.isNotEmpty()

                    /*Button(
                        onClick = { isSyncing = true },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = isReady && !isSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Sync to Cloud", fontWeight = FontWeight.Bold, fontSize = 16.sp
                            )
                        }
                    }*/

                    if (imageBytes != null) {
                        TextButton(
                            onClick = { imageBytes = null },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Clear and retake", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (showImagePicker) {
            PlatformImagePicker(onImagePicked = { bytes ->
                imageBytes = bytes
                showImagePicker = false
            }, onDismiss = {
                showImagePicker = false
            })
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        MainContent()
    }
}
