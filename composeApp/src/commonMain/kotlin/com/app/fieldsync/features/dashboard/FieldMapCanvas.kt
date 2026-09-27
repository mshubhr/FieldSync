package com.app.fieldsync.features.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.app.fieldsync.ui.physics.PhysicsEngine
import com.app.fieldsync.ui.physics.Vector2
import kotlinx.coroutines.isActive
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FieldMapCanvas(
    modifier: Modifier = Modifier,
    workerCount: Int = 6,
    onWorkerSelected: (WorkerMarker) -> Unit = {}
) {

    var canvasSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val physics = remember {
        PhysicsEngine()
    }

    val workers = remember {
        mutableStateListOf<WorkerMarker>()
    }

    val pulses = remember {
        mutableStateListOf<SyncPulse>()
    }

    var elapsedTime by remember {
        mutableStateOf(0f)
    }

    var lastFrameTime by remember {
        mutableStateOf(0L)
    }

    LaunchedEffect(canvasSize) {

        if (canvasSize.width <= 0 || canvasSize.height <= 0) {
            return@LaunchedEffect
        }

        if (workers.isEmpty()) {

            val random = Random(42)

            repeat(workerCount) { index ->

                val x = 70f + random.nextFloat() * (canvasSize.width - 140f).coerceAtLeast(1f)

                val y = 80f + random.nextFloat() * (canvasSize.height - 160f).coerceAtLeast(1f)

                workers.add(
                    WorkerMarker(
                        id = "worker-$index",
                        name = "Worker ${index + 1}",
                        initialPosition = Vector2(
                            x = x, y = y
                        )
                    )
                )
            }
        }
    }

    LaunchedEffect(Unit) {

        while (isActive) {

            withFrameNanos { frameTime ->

                if (lastFrameTime == 0L) {
                    lastFrameTime = frameTime
                }

                val delta = (frameTime - lastFrameTime) / 1_000_000_000f

                lastFrameTime = frameTime

                elapsedTime += delta

                workers.forEach { worker ->

                    worker.update(
                        physics = physics, time = elapsedTime
                    )
                }

                val pulseDelta = delta * 0.65f

                val updatedPulses = pulses.map {
                    it.next(pulseDelta)
                }.filterNot {
                    it.isFinished()
                }

                pulses.clear()
                pulses.addAll(updatedPulses)
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize().pointerInput(Unit) {

        detectDragGestures(

            onDragStart = { position ->

                physics.onPointerDown(
                    Vector2(
                        position.x, position.y
                    )
                )
            },

            onDrag = { change, dragAmount ->

                change.consume()

                physics.onPointerMove(
                    position = Vector2(
                        change.position.x, change.position.y
                    ), velocity = Vector2(
                        dragAmount.x, dragAmount.y
                    )
                )
            },

            onDragEnd = {

                physics.onPointerUp()
            },

            onDragCancel = {

                physics.onPointerUp()
            })
    }.pointerInput(workers) {

        detectTapGestures { position ->

            val tappedWorker = workers.minByOrNull { worker ->

                val dx = worker.position.x - position.x

                val dy = worker.position.y - position.y

                dx * dx + dy * dy
            }

            if (tappedWorker != null) {

                val dx = tappedWorker.position.x - position.x

                val dy = tappedWorker.position.y - position.y

                if (dx * dx + dy * dy < 900f) {

                    onWorkerSelected(
                        tappedWorker
                    )
                }
            }
        }
    }.onSizeChanged {
        canvasSize = it
    }) {

        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF071A16), Color(0xFF0B2920), Color(0xFF0F3D2E)
                )
            )
        )

        val rowSpacing = 28f

        var rowY = 0f

        while (rowY < height) {

            val path = Path()

            val amplitude = 4f

            path.moveTo(
                0f, rowY
            )

            var x = 0f

            while (x <= width) {

                val wave = sin(
                    x * 0.018f + elapsedTime * 0.5f + rowY
                ) * amplitude

                path.lineTo(
                    x, rowY + wave
                )

                x += 20f
            }

            drawPath(
                path = path, color = Color.White.copy(
                    alpha = 0.035f
                )
            )

            rowY += rowSpacing
        }

        val verticalSpacing = 90f

        var gridX = 30f

        while (gridX < width) {

            drawLine(
                color = Color.White.copy(
                    alpha = 0.025f
                ), start = Offset(
                    gridX, 0f
                ), end = Offset(
                    gridX, height
                ), strokeWidth = 1f
            )

            gridX += verticalSpacing
        }

        physics.pointer?.let { pointer ->

            val center = Offset(
                pointer.x, pointer.y
            )

            for (i in 1..3) {

                val radius = 35f * i + sin(
                    elapsedTime * 3f
                ) * 4f

                drawCircle(
                    color = Color(0xFF38BDF8).copy(
                        alpha = 0.08f / i
                    ),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1f
                    )
                )
            }
        }

        if (workers.size >= 2) {

            for (index in 0 until workers.size - 1) {

                val first = workers[index]

                val second = workers[index + 1]

                val start = Offset(
                    first.position.x, first.position.y
                )

                val end = Offset(
                    second.position.x, second.position.y
                )

                val middle = Offset(
                    (start.x + end.x) / 2f, (start.y + end.y) / 2f
                )

                val interaction = physics.pointer

                val bend = interaction?.let {

                    val dx = middle.x - it.x

                    val dy = middle.y - it.y

                    val distance = kotlin.math.sqrt(
                        dx * dx + dy * dy
                    )

                    if (distance < 160f) {

                        val strength = 1f - distance / 160f

                        Offset(
                            dx * strength * 0.5f, dy * strength * 0.5f
                        )

                    } else {
                        Offset.Zero
                    }

                } ?: Offset.Zero

                val control = Offset(
                    middle.x + bend.x, middle.y + bend.y
                )

                val path = Path()

                path.moveTo(
                    start.x, start.y
                )

                path.quadraticTo(
                    control.x, control.y, end.x, end.y
                )

                drawPath(
                    path = path,
                    color = Color(0xFF38BDF8).copy(alpha = 0.12f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2f
                    )
                )
            }
        }

        pulses.forEach { pulse ->

            val position = pulse.currentPosition()

            val alpha = 1f - pulse.progress

            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = alpha), radius = 5f, center = position
            )

            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = alpha * 0.25f),
                radius = 12f,
                center = position
            )
        }

        workers.forEach { worker ->

            val position = Offset(
                worker.position.x, worker.position.y
            )

            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.12f), radius = 20f, center = position
            )

            drawCircle(
                color = Color(0xFF38BDF8), radius = 7f, center = position
            )

            drawCircle(
                color = Color.White, radius = 2.5f, center = position
            )
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent, Color(0xFF020617).copy(
                        alpha = 0.35f
                    )
                ), startY = height * 0.65f, endY = height
            )
        )
    }
}