package uk.co.nickthecoder.tickle.demo


import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.DirectionMovement

class Bee : AbstractRole() {

    val toggle = Resources.instance.input("toggle")
    val left = Resources.instance.input("left")
    val right = Resources.instance.input("right")
    val up = Resources.instance.input("up")
    val down = Resources.instance.input("down")

    val xyMovement = object : Action {
        override fun act(actor: Actor): Boolean {

            if (left.isPressed()) {
                actor.x -= 5
            }
            if (right.isPressed()) {
                actor.x += 5
            }
            if (up.isPressed()) {
                actor.y += 5
            }
            if (down.isPressed()) {
                actor.y -= 5
            }
            return false
        }
    }

    val directionMovement = object : DirectionMovement(
            this,
            speedDegrees = 0.0,
            maxSpeed = 10f,
            minSpeed = -10f,
            maxRotationDegrees = 5.0) {

        override fun act(actor: Actor): Boolean {

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

    private var activeMovement: Action = directionMovement

    override fun tick() {
        if (toggle.isPressed()) {
            activeMovement = if (activeMovement === xyMovement) directionMovement else xyMovement
        }
        activeMovement.act(actor)
    }
}
