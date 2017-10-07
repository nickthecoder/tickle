package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.util.Heading
import uk.co.nickthecoder.tickle.events.Input

class GradualTurnInput<T>(
        heading: Heading,
        var accelerationDegrees: Double,
        var maxTurningSpeedDegrees: Double,
        var drag: Double = 0.0,
        left: String = "left",
        right: String = "right")

    : Turn<T>(heading, 0.0) {

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput

    override fun act(target: T): Boolean {

        if (left.isPressed()) {
            turningSpeedDegrees += accelerationDegrees
        } else if (right.isPressed()) {
            turningSpeedDegrees -= accelerationDegrees
        }
        turningSpeedDegrees *= (1 - drag)
        turningSpeedDegrees = Math.max(Math.min(turningSpeedDegrees, maxTurningSpeedDegrees), -maxTurningSpeedDegrees)

        return super.act(target)
    }

}
