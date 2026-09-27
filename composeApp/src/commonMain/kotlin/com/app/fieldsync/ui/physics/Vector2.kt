package com.app.fieldsync.ui.physics

import kotlin.math.sqrt

data class Vector2(
    val x: Float = 0f, val y: Float = 0f
) {

    operator fun plus(other: Vector2): Vector2 = Vector2(
        x = x + other.x, y = y + other.y
    )

    operator fun minus(other: Vector2): Vector2 = Vector2(
        x = x - other.x, y = y - other.y
    )

    operator fun times(value: Float): Vector2 = Vector2(
        x = x * value, y = y * value
    )

    fun distanceTo(other: Vector2): Float {
        val dx = x - other.x
        val dy = y - other.y

        return sqrt(dx * dx + dy * dy)
    }

    fun length(): Float = sqrt(x * x + y * y)

    fun normalized(): Vector2 {
        val length = length()

        if (length == 0f) return Vector2()

        return Vector2(
            x = x / length, y = y / length
        )
    }
}