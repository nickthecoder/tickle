package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class Kill(val actor: Actor) : Action {

    override fun begin(): Boolean {
        actor.die()
        return true
    }

    override fun act(): Boolean {
        System.err.println("Warning Kill.act should never be called. Actor=$actor")
        return true
    }
}
