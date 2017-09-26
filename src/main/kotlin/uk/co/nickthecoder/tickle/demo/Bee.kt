package uk.co.nickthecoder.tickle.demo


import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Resources

class Bee : AbstractRole() {

    val left = Resources.instance.input("left")
    val right = Resources.instance.input("right")
    val up = Resources.instance.input("up")
    val down = Resources.instance.input("down")

    override fun tick() {
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


    }
}
