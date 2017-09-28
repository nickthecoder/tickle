package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources

class XYControls(
        var speed: Float,
        left: String? = "left",
        right: String? = "right",
        up: String? = "up",
        down: String? = "down")

    : Action<Actor> {

    val left = Resources.instance.optionalInput(left)
    val right = Resources.instance.optionalInput(right)
    val up = Resources.instance.optionalInput(up)
    val down = Resources.instance.optionalInput(down)

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
