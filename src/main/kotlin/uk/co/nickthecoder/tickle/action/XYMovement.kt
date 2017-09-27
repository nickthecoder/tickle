package uk.co.nickthecoder.tickle.action

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.Actor

open class XYMovement(
        var velocity: Vector2f,
        var drag: Float = 0f)

    : Action {

    override fun act(actor: Actor): Boolean {

        if (drag != 0f) {
            velocity.mul(1 - drag)
        }
        actor.x += velocity.x
        actor.y += velocity.y

        return false
    }
}

open class AcceleratedXYMovement(
        velocity: Vector2f,
        drag: Float = 0f,
        var acceleration: Vector2f)
    : XYMovement(velocity, drag) {

    override fun act(actor: Actor): Boolean {
        velocity.add(acceleration)
        super.act(actor)

        return false
    }

}
