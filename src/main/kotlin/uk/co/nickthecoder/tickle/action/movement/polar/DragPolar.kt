package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2f

/**
 * Reduces the speed by applying a constant scaling of (1-drag).
 */
open class DragPolar(
        val velocity: Polar2f,
        drag: Float)

    : Action {

    var oneMinusDrag: Float = 1 - drag

    var drag: Float
        get() = 1 - oneMinusDrag
        set(v) {
            oneMinusDrag = 1 - v
        }

    override fun act(): Boolean {
        velocity.magnitude *= oneMinusDrag
        return false
    }

}
