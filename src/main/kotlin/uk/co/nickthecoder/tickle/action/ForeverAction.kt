package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class ForeverAction(val child: Action) : Action {

    override fun begin(actor: Actor) {
        child.begin(actor)
    }

    override fun act(actor: Actor): Boolean {
        if (child.act(actor)) {
            child.begin(actor)
        }
        return false
    }
}
