package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action


class Hand : Controllable() {

    override val movement = object : Action<Actor> {
        override fun act(target: Actor): Boolean {

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
