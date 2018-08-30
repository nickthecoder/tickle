package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.action.Action

open class Accelerate(
        val velocity: Vector2f,
        val acceleration: Vector2f)

    : Action {

    override fun act(): Boolean {
        velocity.add(acceleration)
        return false
    }
}
