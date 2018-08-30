package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle

/**
 * Changes the heading constantly forever.
 */
open class Circle(
        val heading: Angle,
        val turningSpeed: Angle)

    : Action {

    override fun act(): Boolean {
        heading.radians += turningSpeed.radians
        return false
    }

}