package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class Die : Action<Actor> {

    override fun begin(target: Actor): Boolean {
        target.die()
        return true
    }

    override fun act(target: Actor): Boolean {
        System.err.println("Warning Die.act should never be called. Actor=$target")
        return true
    }
}
