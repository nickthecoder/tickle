package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.util.Angle

/**
 * Changes a heading by fixed amount when keys are pressed. For a more natural turn, see [GradualTurnInput].
 */
class TurnInput(
        val heading: Angle,
        val turningSpeed: Angle,
        left: String = "left",
        right: String = "right")

    : Action {

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput

    override fun act(): Boolean {

        if (left.isPressed()) {
            heading.radians += turningSpeed.radians
        } else if (right.isPressed()) {
            heading.radians -= turningSpeed.radians
        }

        return false
    }

}
