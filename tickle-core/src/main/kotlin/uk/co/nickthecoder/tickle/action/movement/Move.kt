package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.action.Action

open class Move(
        val position: Vector2f,
        val velocity: Vector2f)

    : Action {

    override fun act(): Boolean {
        position.x += velocity.x
        position.y += velocity.y
        return false
    }
}
