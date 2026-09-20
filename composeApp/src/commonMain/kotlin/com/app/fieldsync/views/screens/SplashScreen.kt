package com.app.fieldsync.views.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f, animationSpec = tween(durationMillis = 1000)
        )
        delay(300.milliseconds)
        animationProgress.animateTo(
            targetValue = 2f, animationSpec = tween(durationMillis = 1200)
        )
        delay(400.milliseconds)
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val progress = animationProgress.value

        Canvas(modifier = Modifier.size(240.dp)) {
            val width = size.width
            val height = size.height

            val stemProgress = progress.coerceIn(0f, 0.5f) * 2f
            if (stemProgress > 0f) {
                drawLine(
                    color = Color.White,
                    start = Offset(width * 0.3f, height * 0.2f),
                    end = Offset(width * 0.3f, height * 0.2f + (height * 0.6f * stemProgress)),
                    strokeWidth = 14f,
                    cap = StrokeCap.Round
                )
            }

            val line1Progress = (progress - 0.3f).coerceIn(0f, 0.5f) * 2f
            if (line1Progress > 0f) {
                val morphOffset = if (progress > 1f) (progress - 1f) * 60f else 0f
                drawLine(
                    color = Color.White,
                    start = Offset(width * 0.3f + morphOffset, height * 0.2f + morphOffset),
                    end = Offset(
                        width * 0.3f + (width * 0.4f * line1Progress) + morphOffset,
                        height * 0.2f + morphOffset
                    ),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            }

            val line2Progress = (progress - 0.5f).coerceIn(0f, 0.5f) * 2f
            if (line2Progress > 0f) {
                val morphOffsetX = if (progress > 1f) (progress - 1f) * 90f else 0f
                val morphOffsetY = if (progress > 1f) (progress - 1f) * 140f else 0f
                drawLine(
                    color = Color.Cyan,
                    start = Offset(width * 0.3f + morphOffsetX, height * 0.45f + morphOffsetY),
                    end = Offset(
                        width * 0.3f + (width * 0.3f * line2Progress) + morphOffsetX,
                        height * 0.45f + morphOffsetY
                    ),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            }
        }

        if (progress > 1.2f) {
            Text(
                text = "SYSTEM ARCHITECTURE INITIALIZED",
                color = Color.Gray.copy(alpha = (progress - 1.2f).coerceIn(0f, 1f)),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}