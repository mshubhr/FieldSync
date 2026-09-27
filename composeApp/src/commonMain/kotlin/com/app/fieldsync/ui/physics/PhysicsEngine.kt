package com.app.fieldsync.ui.physics

import kotlin.math.max
import kotlin.math.min

class PhysicsEngine {

    var pointer: Vector2? = null
        private set

    var pointerVelocity: Vector2 = Vector2()
        private set

    fun onPointerDown(position: Vector2) {
        pointer = position
        pointerVelocity = Vector2()
    }

    fun onPointerMove(
        position: Vector2, velocity: Vector2
    ) {
        pointer = position
        pointerVelocity = velocity
    }

    fun onPointerUp() {
        pointer = null
        pointerVelocity = Vector2()
    }

    fun interactionStrength(
        position: Vector2, radius: Float
    ): Float {

        val currentPointer = pointer ?: return 0f

        val distance = position.distanceTo(currentPointer)

        if (distance >= radius) {
            return 0f
        }

        return 1f - (distance / radius)
    }

    fun pushAmount(
        position: Vector2, radius: Float, strength: Float
    ): Vector2 {

        val currentPointer = pointer ?: return Vector2()

        val direction = position - currentPointer

        val distance = max(direction.length(), 0.001f)

        val normalized = direction * (1f / distance)

        val factor = interactionStrength(
            position = position, radius = radius
        )

        val pointerPush = pointerVelocity * 0.12f

        return normalized * (strength * factor) + pointerPush * factor
    }

    fun clamp(
        value: Float, minValue: Float, maxValue: Float
    ): Float {
        return min(max(value, minValue), maxValue)
    }
}