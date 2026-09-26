package com.app.fieldsync.views.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fieldsync.reports.ReportRepository
import com.app.fieldsync.reports.SyncLogEntry
import com.app.fieldsync.reports.SyncNetworkMode
import com.app.fieldsync.views.components.StatCard
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncLabScreen(
    reportRepository: ReportRepository, onBack: () -> Unit
) {
    var networkMode by remember { mutableStateOf(reportRepository.networkMode) }
    var pendingCount by remember { mutableStateOf(0) }
    var syncedCount by remember { mutableStateOf(0) }
    var failedCount by remember { mutableStateOf(0) }
    var conflictCount by remember { mutableStateOf(0) }
    var isRunningSync by remember { mutableStateOf(false) }

    val logs = remember { mutableStateListOf<SyncLogEntry>() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val logScrollState = rememberScrollState()

    fun addLog(msg: String, level: SyncLogEntry.LogLevel = SyncLogEntry.LogLevel.INFO) {
        val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val h = time.hour.toString().padStart(2, '0')
        val m = time.minute.toString().padStart(2, '0')
        val s = time.second.toString().padStart(2, '0')
        logs.add(SyncLogEntry("$h:$m:$s", msg, level))
    }

    suspend fun refreshCounts() {
        pendingCount = reportRepository.getPendingCount()
        syncedCount = reportRepository.getSyncedCount()
    }

    LaunchedEffect(Unit) {
        refreshCounts()
        addLog("Sync Lab initialized. Mode: ${networkMode.displayName}", SyncLogEntry.LogLevel.INFO)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "SYNC LAB",
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }, actions = {
                    IconButton(onClick = {
                        scope.launch { refreshCounts() }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Counts",
                            tint = Color.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFF0F172A)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth().verticalScroll(scrollState)
                    .padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Network Simulation Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            val (statusColor, statusText) = when (networkMode) {
                                SyncNetworkMode.ONLINE -> Color(0xFF4CAF50) to "ONLINE"
                                SyncNetworkMode.OFFLINE -> Color(0xFFEF5350) to "OFFLINE"
                                SyncNetworkMode.SLOW_NETWORK -> Color(0xFFFFCA28) to "SLOW NETWORK"
                                SyncNetworkMode.FORCE_FAILURE -> Color(0xFFFF7043) to "FORCE FAILURE"
                                SyncNetworkMode.FORCE_CONFLICT -> Color(0xFFAB47BC) to "FORCE CONFLICT"
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape)
                                        .background(statusColor)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    statusText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            networkMode.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )

                        Spacer(Modifier.height(16.dp))

                        @OptIn(ExperimentalFlexBoxApi::class) FlexBox(
                            config = {
                                gap(8.dp)
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            SyncNetworkMode.entries.forEach { mode ->
                                val isSelected = networkMode == mode
                                Button(
                                    onClick = {
                                        networkMode = mode
                                        reportRepository.networkMode = mode
                                        addLog(
                                            "Network mode changed to ${mode.displayName}",
                                            SyncLogEntry.LogLevel.WARNING
                                        )
                                    }, colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) Color(0xFF38BDF8) else Color(
                                            0xFF334155
                                        ),
                                        contentColor = if (isSelected) Color.Black else Color.White
                                    ), shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        mode.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Sync Engine Metrics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(16.dp))

                        @OptIn(ExperimentalGridApi::class) Grid(
                            config = {
                                val cols = 2
                                repeat(cols) { column(1.dp) }
                                repeat(2) { row(90.dp) }
                                gap(12.dp)
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            StatCard(
                                "Pending Operations", "$pendingCount", Modifier, Color(0xFFFF9800)
                            )
                            StatCard("Successful", "$syncedCount", Modifier, Color(0xFF4CAF50))
                            StatCard("Failed", "$failedCount", Modifier, Color(0xFFEF5350))
                            StatCard("Conflicts", "$conflictCount", Modifier, Color(0xFFAB47BC))
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Lab Operations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val created = reportRepository.createTestRecords(5)
                                        addLog(
                                            "Created 5 offline test records (IDs: ${created.joinToString()})",
                                            SyncLogEntry.LogLevel.INFO
                                        )
                                        refreshCounts()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(
                                        0xFF38BDF8
                                    )
                                ),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8))
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("+ 5 Records", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val created = reportRepository.createTestRecords(10)
                                        addLog(
                                            "Created 10 offline test records (IDs: ${created.joinToString()})",
                                            SyncLogEntry.LogLevel.INFO
                                        )
                                        refreshCounts()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(
                                        0xFF38BDF8
                                    )
                                ),
                                border = BorderStroke(
                                    1.dp, Color(0xFF38BDF8)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("+ 10 Records", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    isRunningSync = true
                                    val result = reportRepository.syncAllPendingReports { log ->
                                        logs.add(log)
                                    }
                                    failedCount += result.failureCount
                                    conflictCount += result.conflictCount
                                    refreshCounts()
                                    isRunningSync = false
                                }
                            },
                            enabled = !isRunningSync && pendingCount > 0,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF22C55E),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        ) {
                            if (isRunningSync) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Syncing...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "RUN SYNC ($pendingCount Pending)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF020617))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Live Execution Logs",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )

                            Text(
                                "Clear",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.clickable { logs.clear() }.padding(4.dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                                .background(Color.Black, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .padding(12.dp).verticalScroll(logScrollState)
                        ) {
                            if (logs.isEmpty()) {
                                Text(
                                    "> Terminal ready. Perform an operation to see live logs.",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    logs.forEach { entry ->
                                        val logColor = when (entry.level) {
                                            SyncLogEntry.LogLevel.INFO -> Color(0xFF38BDF8)
                                            SyncLogEntry.LogLevel.SUCCESS -> Color(0xFF4ADE80)
                                            SyncLogEntry.LogLevel.WARNING -> Color(0xFFFACC15)
                                            SyncLogEntry.LogLevel.ERROR -> Color(0xFFF87171)
                                        }

                                        Text(
                                            "[${entry.timestamp}] ${entry.message}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = logColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}