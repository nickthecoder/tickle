package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Scalar

open class DragPolar(
        val speed: Scalar,
        drag: Float)

    : Action<Actor> {

    var oneMinusDrag: Float = 1 - drag

    var drag: Float
        get() = 1 - oneMinusDrag
        set(v) {
            oneMinusDrag = 1 - v
        }

    override fun act(target: Actor): Boolean {
        speed.value *= oneMinusDrag
        return false
    }

}
