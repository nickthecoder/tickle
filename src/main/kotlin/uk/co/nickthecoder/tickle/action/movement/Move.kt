package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action

open class Move(
        val velocity: Vector2f)

    : Action<Actor> {

    override fun act(target: Actor): Boolean {
        target.x += velocity.x
        target.y += velocity.y
        return false
    }
}
