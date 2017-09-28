package uk.co.nickthecoder.tickle.demo


import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.DirectionMovement

class Bee : Controllable() {

    override val movement = object : DirectionMovement(
            this,
            speedDegrees = 0.0,
            maxSpeed = 10f,
            minSpeed = -10f,
            maxRotationDegrees = 5.0) {

        override fun act(target: Actor): Boolean {

            if (left.isPressed()) {
                speedDegrees += 1
            } else if (right.isPressed()) {
                speedDegrees -= 1
            } else {
                // Automatically level off when a key's not pressed.
                speedDegrees *= 0.93
            }

            if (up.isPressed()) {
                speed += 0.5f
            }
            if (down.isPressed()) {
                speed -= 0.5f
            }
            return super.act(actor)
        }
    }

}
