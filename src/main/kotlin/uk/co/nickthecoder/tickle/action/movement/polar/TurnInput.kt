package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.util.Angle

class TurnInput<T>(
        heading: Angle,
        turningSpeed : Double,
        left: String = "left",
        right: String = "right")

    : Turn(heading, Angle()) {

    val ts = turningSpeed

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput

    override fun act(): Boolean {

        if (left.isPressed()) {
            turningSpeed.degrees = ts
        } else if (right.isPressed()) {
            turningSpeed.degrees = -ts
        }

        return super.act()
    }

}
