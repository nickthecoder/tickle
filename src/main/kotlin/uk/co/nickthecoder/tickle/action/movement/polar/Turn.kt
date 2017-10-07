package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Heading

open class Turn<T>(
        val heading: Heading,
        var turningSpeedDegrees: Double)

    : Action<T> {

    override fun act(target: T): Boolean {
        heading.degrees += turningSpeedDegrees
        return false
    }

}
