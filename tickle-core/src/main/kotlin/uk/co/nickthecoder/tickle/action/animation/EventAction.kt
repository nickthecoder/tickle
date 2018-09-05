package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action

class EventAction(val actor: Actor, val eventName: String)
    : Action {

    override fun act(): Boolean {
        actor.event(eventName)
        return true
    }
}
