package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class EventAction(val actor: Actor, val eventName: String)
    : Action {

    override fun act(): Boolean {
        actor.event(eventName)
        return true
    }
}
