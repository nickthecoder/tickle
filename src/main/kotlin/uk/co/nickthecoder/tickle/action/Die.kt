package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class Die : Action {

    override fun begin(actor: Actor): Boolean {
        actor.die()
        return true
    }

    override fun act(actor: Actor): Boolean {
        System.err.println("Warning Die.act should never be called. Actor=$actor")
        return true
    }
}
