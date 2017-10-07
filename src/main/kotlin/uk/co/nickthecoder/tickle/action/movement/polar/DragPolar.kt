package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Scalar

open class DragPolar(
        val speed: Scalar,
        drag: Float)

    : Action {

    var oneMinusDrag: Float = 1 - drag

    var drag: Float
        get() = 1 - oneMinusDrag
        set(v) {
            oneMinusDrag = 1 - v
        }

    override fun act(): Boolean {
        speed.value *= oneMinusDrag
        return false
    }

}
