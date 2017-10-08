package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.Action

class Drag(
        val velocity: Vector2d,
        var drag: Double)

    : Action {


    override fun act(): Boolean {
        if (drag != 0.0) {
            velocity.mul(1 - drag)
        }
        return false
    }
}
