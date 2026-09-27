package com.app.fieldsync.features.dashboard

import com.app.fieldsync.ui.physics.SpringValue
import com.app.fieldsync.ui.physics.Vector2
import kotlin.math.sin

class WorkerMarker(
    val id: String, val name: String, initialPosition: Vector2
) {

    val basePosition = initialPosition

    private val offsetX = SpringValue(
        stiffness = 0.055f, damping = 0.86f
    )

    private val offsetY = SpringValue(
        stiffness = 0.055f, damping = 0.86f
    )

    var position: Vector2 = initialPosition
        private set

    fun update(
        physics: com.app.fieldsync.ui.physics.PhysicsEngine, time: Float
    ) {

        val interaction = physics.pushAmount(
            position = basePosition, radius = 130f, strength = 25f
        )

        offsetX.addVelocity(interaction.x)
        offsetY.addVelocity(interaction.y)

        val idleX = sin(time * 1.2f + id.hashCode()) * 0.4f
        val idleY = sin(time * 1.5f + id.hashCode()) * 0.4f

        offsetX.update(idleX)
        offsetY.update(idleY)

        position = Vector2(
            x = basePosition.x + offsetX.value, y = basePosition.y + offsetY.value
        )
    }
}