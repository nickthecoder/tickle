package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action

abstract class Controllable : AbstractRole() {

    var hasInput: Boolean = false

    abstract val movement: Action<Actor>

    override fun activated() {
        movement.begin(actor)
    }

    override fun tick() {
        if (hasInput) {
            movement.act(actor)
        }
    }
}
