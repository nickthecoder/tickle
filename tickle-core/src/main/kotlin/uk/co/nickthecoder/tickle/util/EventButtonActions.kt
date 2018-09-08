package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.EventAction

/**
 * Sends events to the Button's Actor.
 * These events can cause the Pose to change, or make a sound effect etc.
 *
 * The event names used are "down", "up" and "clicked"
 */
class EventButtonActions : ButtonActions {

    override fun downAction(button: Button): Action? {
        return EventAction(button.actor, "down")
    }

    override fun upAction(button: Button): Action? {
        return EventAction(button.actor, "up")
    }


    override fun clickedAction(button: Button): Action? {
        return EventAction(button.actor, "clicked")
    }
}