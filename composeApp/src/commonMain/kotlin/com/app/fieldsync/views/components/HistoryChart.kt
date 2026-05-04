package com.app.fieldsync.views.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fieldsync.models.RamEntry
import kotlinx.datetime.number

@Composable
fun HistoryChart(
    entries: List<RamEntry>, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp)
            .background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        Text(
            text = "RAM Usage History (KB)",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No data available", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            val maxVal = entries.maxOf { it.sizeKb }.toFloat().coerceAtLeast(1f)

            Row(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                Column(
                    modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("${maxVal.toInt()}", fontSize = 10.sp, color = Color.Gray)
                    Text("${(maxVal / 2).toInt()}", fontSize = 10.sp, color = Color.Gray)
                    Text("0", fontSize = 10.sp, color = Color.Gray)
                }

                Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    val width = size.width
                    val height = size.height

                    for (i in 0..2) {
                        val y = height * i / 2
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val spacing = if (entries.size > 1) width / (entries.size - 1) else 0f

                    val points = entries.mapIndexed { index, entry ->
                        Offset(
                            x = index * spacing,
                            y = height - (entry.sizeKb.toFloat() / maxVal * height)
                        )
                    }

                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points.first().x, points.first().y)
                            points.forEach { lineTo(it.x, it.y) }
                        }
                    }

                    drawPath(
                        path = path, color = Color.Black, style = Stroke(width = 2.dp.toPx())
                    )

                    points.forEach { point ->
                        drawCircle(
                            color = Color.Black, radius = 3.dp.toPx(), center = point
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (entries.isNotEmpty()) {
                    Text(
                        text = "${entries.first().date.day}/${entries.first().date.month.number}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    if (entries.size > 2) {
                        val midIndex = entries.size / 2
                        val midEntry = entries[midIndex]
                        Text(
                            text = "${midEntry.date.day}/${midEntry.date.month.number}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    if (entries.size > 1) {
                        Text(
                            text = "${entries.last().date.day}/${entries.last().date.month.number}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
