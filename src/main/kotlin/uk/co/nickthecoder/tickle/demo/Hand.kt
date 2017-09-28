package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action


class Hand : Controlable() {

    override val movement = object : Action {
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

}
