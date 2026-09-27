package com.app.fieldsync.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectionStatus(
    online: Boolean, modifier: Modifier = Modifier
) {
    val color = if (online) Color(0xFF4ADE80) else Color(0xFFF87171)
    val icon = if (online) Icons.Default.CloudDone else Icons.Default.CloudOff

    Row(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = if (online) "ONLINE" else "OFFLINE",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FieldStatusLabel(
    modifier: Modifier = Modifier, workerCount: Int = 7
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF020617).copy(alpha = 0.65f))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Text(
            text = "LIVE FIELD MAP",
            color = Color(0xFF38BDF8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$workerCount workers active", color = Color.White, fontSize = 12.sp
        )
    }
}

@Composable
fun DashboardStat(
    modifier: Modifier = Modifier, icon: @Composable () -> Unit, value: String, label: String
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF020617).copy(alpha = 0.72f)).padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp
        )
    }
}

@Composable
fun SelectedWorkerCard(
    worker: WorkerMarker, onDeselect: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column {
                    Text(
                        text = worker.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Active • GPS Position (Physics Engine)",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = onDeselect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Deselect",
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}