package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle

open class Turn<T>(
        val heading: Angle,
        var turningSpeedDegrees: Double)

    : Action {

    override fun act(): Boolean {
        heading.degrees += turningSpeedDegrees
        return false
    }

}
