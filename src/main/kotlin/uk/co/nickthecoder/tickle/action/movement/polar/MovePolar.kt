package uk.co.nickthecoder.tickle.action.movement.polar

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2f

class MovePolar(
        val position: Vector2f,
        val velocity: Polar2f)

    : Action {

    override fun act(): Boolean {
        position.add(velocity.vector())
        return false
    }
}
