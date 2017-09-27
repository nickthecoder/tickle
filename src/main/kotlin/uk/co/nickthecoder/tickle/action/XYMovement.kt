package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.math.Vector2

open class XYMovement(
        var velocity: Vector2,
        var drag: Float = 1f)

    : Action {

    override fun act(actor: Actor): Boolean {

        if (drag != 1f) {
            velocity = velocity * drag
        }
        actor.x += velocity.x
        actor.y += velocity.y

        return false
    }
}

open class AcceleratedXYMovement(
        velocity: Vector2,
        drag: Float,
        var acceleration: Vector2)
    : XYMovement(velocity, drag) {

    override fun act(actor: Actor): Boolean {
        velocity = velocity + acceleration
        super.act(actor)

        return false
    }

}
