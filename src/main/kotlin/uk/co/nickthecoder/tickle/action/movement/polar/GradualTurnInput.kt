package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.util.Angle

class GradualTurnInput(
        val heading: Angle,
        var acceleration: Angle,
        var maxSpeed: Angle,
        var drag: Double = 0.0,
        left: String = "left",
        right: String = "right")

    : Action {

    val turningSpeed = Angle()

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput

    override fun act(): Boolean {

        if (left.isPressed()) {
            turningSpeed.radians += acceleration.radians
        } else if (right.isPressed()) {
            turningSpeed.radians -= acceleration.radians
        }
        turningSpeed.radians *= (1 - drag)
        turningSpeed.radians = Math.max(Math.min(turningSpeed.radians, maxSpeed.radians), -maxSpeed.radians)

        heading.radians += turningSpeed.radians

        return false
    }

}
