package com.app.fieldsync.features.dashboard

import androidx.compose.ui.geometry.Offset

data class SyncPulse(
    val start: Offset, val end: Offset, val progress: Float = 0f
) {

    fun next(delta: Float): SyncPulse {
        return copy(progress = (progress + delta).coerceAtMost(1f))
    }

    fun currentPosition(): Offset {
        return Offset(
            start.x + (end.x - start.x) * progress, start.y + (end.y - start.y) * progress
        )
    }

    fun isFinished(): Boolean = progress >= 1f
}
