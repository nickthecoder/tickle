package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.Input

class DirectionControls(
        maxSpeed: Float,
        minSpeed: Float = 0f,
        val speedChange: Float = 1f,
        val rotationChange: Double = 1.0,
        val rotationDrag: Double = 0.0,
        maxRotationSpeed: Double,

        left: String? = "left",
        right: String? = "right",
        up: String? = "up",
        down: String? = "down")

    : DirectionMovement(
        speedDegrees = 0.0,
        maxSpeed = maxSpeed,
        minSpeed = minSpeed,
        maxRotationDegrees = maxRotationSpeed) {

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput
    val up = Resources.instance.optionalInput(up) ?: Input.dummyInput
    val down = Resources.instance.optionalInput(down) ?: Input.dummyInput

    override fun act(target: Actor): Boolean {

        if (left.isPressed()) {
            speedDegrees += rotationChange
        } else if (right.isPressed()) {
            speedDegrees -= rotationChange
        } else {
            // Automatically level off when a key's not pressed.
            speedDegrees *= (1 - rotationDrag)
        }

        if (up.isPressed()) {
            speed += speedChange
        }
        if (down.isPressed()) {
            speed -= speedChange
        }

        return super.act(target)
    }
}
