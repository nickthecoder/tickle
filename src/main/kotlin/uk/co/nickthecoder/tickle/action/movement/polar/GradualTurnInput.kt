package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.util.Angle

class GradualTurnInput(
        heading: Angle,
        var accelerationDegrees: Double,
        var maxTurningSpeedDegrees: Double,
        var drag: Double = 0.0,
        left: String = "left",
        right: String = "right")

    : Turn(heading, Angle()) {

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput

    override fun act(): Boolean {

        if (left.isPressed()) {
            turningSpeed.degrees += accelerationDegrees
        } else if (right.isPressed()) {
            turningSpeed.degrees -= accelerationDegrees
        }
        turningSpeed.degrees *= (1 - drag)
        turningSpeed.degrees = Math.max(Math.min(turningSpeed.degrees, maxTurningSpeedDegrees), -maxTurningSpeedDegrees)

        return super.act()
    }

}
