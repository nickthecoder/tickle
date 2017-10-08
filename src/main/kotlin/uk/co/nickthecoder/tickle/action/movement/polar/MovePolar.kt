package uk.co.nickthecoder.tickle.action.movement.polar

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2d

class MovePolar(
        val position: Vector2d,
        val velocity: Polar2d)

    : Action {

    override fun act(): Boolean {
        position.add(velocity.vector())
        return false
    }
}
