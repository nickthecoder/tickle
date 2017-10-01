package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.Input

class XYControls(
        var speed: Float,
        left: String? = "left",
        right: String? = "right",
        up: String? = "up",
        down: String? = "down")

    : Action<Actor> {

    val left = Resources.instance.optionalInput(left) ?: Input.dummyInput
    val right = Resources.instance.optionalInput(right) ?: Input.dummyInput
    val up = Resources.instance.optionalInput(up) ?: Input.dummyInput
    val down = Resources.instance.optionalInput(down) ?: Input.dummyInput

    var xSpeed: Float = speed

    var ySpeed: Float = speed

    override fun act(target: Actor): Boolean {

        if (left.isPressed()) {
            target.x -= xSpeed
        }
        if (right.isPressed()) {
            target.x += xSpeed
        }
        if (up.isPressed()) {
            target.y += ySpeed
        }
        if (down.isPressed()) {
            target.y -= ySpeed
        }
        return false
    }

}
