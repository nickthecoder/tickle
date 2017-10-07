package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action

class Drag(
        val velocity: Vector2f,
        var drag: Float)

    : Action<Actor> {


    override fun act(target: Actor): Boolean {
        if (drag != 0f) {
            velocity.mul(1 - drag)
        }
        return false
    }
}
