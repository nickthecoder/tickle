package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2d

/**
 * Reduces the speed by applying a constant scaling of (1-drag).
 */
open class DragPolar(
        val velocity: Polar2d,
        drag: Double)

    : Action {

    var oneMinusDrag: Double = 1 - drag

    var drag: Double
        get() = 1 - oneMinusDrag
        set(v) {
            oneMinusDrag = 1 - v
        }

    override fun act(): Boolean {
        velocity.magnitude *= oneMinusDrag
        return false
    }

}
