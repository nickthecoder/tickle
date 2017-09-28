package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action

abstract class Controlable : AbstractRole() {

    var hasInput: Boolean = false

    val left = Resources.instance.input("left")
    val right = Resources.instance.input("right")
    val up = Resources.instance.input("up")
    val down = Resources.instance.input("down")

    abstract val movement : Action

    override fun tick() {
        if (hasInput) {
            movement.act(actor)
        }
    }
}
