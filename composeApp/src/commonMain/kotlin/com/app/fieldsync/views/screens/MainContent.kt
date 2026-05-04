package com.app.fieldsync.views.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fieldsync.models.RamEntry
import com.app.fieldsync.reports.ReportRepository
import com.app.fieldsync.views.components.DocumentTypeDropdown
import com.app.fieldsync.views.components.HistoryChart
import com.app.fieldsync.views.components.PlatformImagePicker
import com.app.fieldsync.views.components.StatCard
import io.ktor.util.encodeBase64
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    userName: String = "User",
    onLogout: () -> Unit = {},
    reportRepository: ReportRepository = remember { ReportRepository() }
) {
    var showImagePicker by remember { mutableStateOf(false) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isSyncing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var historyEntries by remember { mutableStateOf(emptyList<RamEntry>()) }

    LaunchedEffect(Unit) {
        val localReports = reportRepository.getLocalReports()
        historyEntries = localReports.map { report ->
            RamEntry(
                sizeKb = (report.imageBase64.length * 0.75 / 1024).toInt(),
                date = kotlin.time.Instant.fromEpochMilliseconds(report.timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
            )
        }
    }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

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
                }, actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.Unspecified
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
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Good morning, $userName",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = "You have ${historyEntries.size} syncs completed today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("Pending", "0", Modifier.weight(1f), Color(0xFFFF9800))
                    StatCard(
                        "Synced", "${historyEntries.size}", Modifier.weight(1f), Color(0xFF4CAF50)
                    )
                    StatCard(
                        "Storage",
                        "${historyEntries.sumOf { it.sizeKb } / 1024} MB",
                        Modifier.weight(1f),
                        Color(0xFF2196F3))
                }

                Text(
                    text = "New Field Report",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                DocumentTypeDropdown(
                    selectedType = selectedCategory, onTypeSelected = { selectedCategory = it })

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Add Field Note") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Describe the captured data...") },
                    shape = RoundedCornerShape(12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
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
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Document Captured",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    val sizeKb = currentImageBytes.size / 1024

                                    Text(
                                        text = "$sizeKb KB • Drag down to Sync",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDragging) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.offset {
                                            IntOffset(
                                                offsetX.roundToInt(), offsetY.roundToInt()
                                            )
                                        }.pointerInput(Unit) {
                                            detectDragGestures(onDragStart = {
                                                isDragging = true
                                            }, onDragEnd = {
                                                isDragging = false
                                                if (offsetY > 100f && !isSyncing && selectedCategory.isNotEmpty()) {
                                                    scope.launch {
                                                        isSyncing = true
                                                        errorMessage = null
                                                        val result = reportRepository.syncReport(
                                                            category = selectedCategory,
                                                            note = note,
                                                            imageBase64 = imageBytes?.encodeBase64()
                                                                ?: ""
                                                        )
                                                        if (result.isSuccess) {
                                                            historyEntries =
                                                                historyEntries + RamEntry(
                                                                    sizeKb,
                                                                    Clock.System.now()
                                                                        .toLocalDateTime(
                                                                            TimeZone.currentSystemDefault()
                                                                        ).date
                                                                )
                                                            imageBytes = null
                                                            selectedCategory = ""
                                                            note = ""
                                                        } else {
                                                            errorMessage =
                                                                result.exceptionOrNull()?.message
                                                                    ?: "Failed to sync report"
                                                        }
                                                        isSyncing = false
                                                    }
                                                }
                                                offsetX = 0f
                                                offsetY = 0f
                                            }, onDragCancel = {
                                                isDragging = false
                                                offsetX = 0f
                                                offsetY = 0f
                                            }, onDrag = { change, dragAmount ->
                                                change.consume()
                                                offsetX += dragAmount.x
                                                offsetY += dragAmount.y
                                            })
                                        }.background(
                                            if (isDragging) Color.Black.copy(alpha = 0.1f) else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        ).padding(4.dp)
                                    )
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
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
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

                HistoryChart(entries = historyEntries)

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = {
                            scope.launch {
                                isSyncing = true
                                errorMessage = null

                                val result = reportRepository.syncReport(
                                    category = selectedCategory,
                                    note = note,
                                    imageBase64 = imageBytes?.encodeBase64() ?: ""
                                )

                                if (result.isSuccess) {
                                    historyEntries = historyEntries + RamEntry(
                                        (imageBytes?.size ?: 0) / 1024,
                                        Clock.System.now()
                                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    )
                                    imageBytes = null
                                    selectedCategory = ""
                                    note = ""
                                } else {
                                    errorMessage =
                                        result.exceptionOrNull()?.message ?: "Failed to sync report"
                                }
                                isSyncing = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = (imageBytes != null && selectedCategory.isNotEmpty()) && !isSyncing,
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
                                "Submit & Sync Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

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
