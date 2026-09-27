package com.app.fieldsync.ui.physics

import kotlin.math.abs

class SpringValue(
    initialValue: Float = 0f,
    private val stiffness: Float = 0.08f,
    private val damping: Float = 0.88f
) {

    var value: Float = initialValue
        private set

    var velocity: Float = 0f
        private set

    fun update(target: Float) {
        velocity += (target - value) * stiffness
        velocity *= damping
        value += velocity

        if (abs(velocity) < 0.001f && abs(target - value) < 0.001f) {
            value = target
            velocity = 0f
        }
    }

    fun addVelocity(amount: Float) {
        velocity += amount
    }

    fun reset(value: Float = 0f) {
        this.value = value
        velocity = 0f
    }
}