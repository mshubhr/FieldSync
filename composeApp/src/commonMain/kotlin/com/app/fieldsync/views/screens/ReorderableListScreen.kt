package com.app.fieldsync.views.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ReorderableListScreen() {
    var items by remember {
        mutableStateOf(List(10) { "Item ${it + 1}" })
    }

    val lazyListState = rememberLazyListState()

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }

    var draggingOffset by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Hold and Drag to Reorder",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items, key = { _, item -> item }) { index, item ->
                val isDragging = draggedItemIndex == index

                val zIndex = if (isDragging) 1f else 0f
                val elevation by animateDpAsState(if (isDragging) 12.dp else 2.dp)

                Card(
                    modifier = Modifier.fillMaxWidth().zIndex(zIndex).animateItem()
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(onDragStart = {
                                draggedItemIndex = index
                                draggingOffset = 0f
                            }, onDrag = { change, dragAmount ->
                                change.consume()
                                draggingOffset += dragAmount.y

                                val currentDraggedIndex =
                                    draggedItemIndex ?: return@detectDragGesturesAfterLongPress

                                val threshold = 64.dp.toPx()
                                val indexChange = (draggingOffset / threshold).toInt()

                                if (indexChange != 0) {
                                    val targetIndex = (currentDraggedIndex + indexChange).coerceIn(
                                        0, items.size - 1
                                    )

                                    if (targetIndex != currentDraggedIndex) {
                                        items = items.toMutableList().apply {
                                            add(targetIndex, removeAt(currentDraggedIndex))
                                        }
                                        draggedItemIndex = targetIndex
                                        draggingOffset = 0f
                                    }
                                }
                            }, onDragEnd = {
                                draggedItemIndex = null
                                draggingOffset = 0f
                            }, onDragCancel = {
                                draggedItemIndex = null
                                draggingOffset = 0f
                            })
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDragging) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isDragging) FontWeight.Bold else null
                        )
                        Icon(
                            imageVector = Icons.Default.Reorder,
                            contentDescription = "Reorder Icon",
                            tint = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
