package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.math.Vector2

open class XYMovement(
        val actor: Actor,
        var velocity: Vector2,
        var drag: Float = 1f)

    : Action {

    override fun tick() {

        if (drag != 1f) {
            velocity = velocity * drag
        }
        actor.x += velocity.x
        actor.y += velocity.y
    }
}

open class AcceleratedXYMovement(
        actor: Actor,
        velocity: Vector2,
        drag: Float,
        var acceleration: Vector2)
    : XYMovement(actor, velocity, drag) {

    override fun tick() {
        velocity = velocity + acceleration
        super.tick()
    }

}
