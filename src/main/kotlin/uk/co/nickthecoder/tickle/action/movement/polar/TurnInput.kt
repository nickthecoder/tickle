package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.util.Heading
import uk.co.nickthecoder.tickle.events.Input

class TurnInput<T>(
        heading: Heading,
        turningSpeed : Double,
        left: String = "left",
        right: String = "right")

    : Turn<T>(heading, 0.0) {

    val ts = turningSpeed

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput

    override fun act(target: T): Boolean {

        if (left.isPressed()) {
            turningSpeedDegrees = ts
        } else if (right.isPressed()) {
            turningSpeedDegrees = -ts
        }

        return super.act(target)
    }

}
