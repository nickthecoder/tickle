package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class Die : Action {

    override fun act(actor: Actor): Boolean {
        actor.die()
        return true
    }
}
