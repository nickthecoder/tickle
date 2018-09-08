package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.EventAction

/**
 * Sends events to the Button's Actor.
 * These events can cause the Pose to change, or make a sound effect etc.
 *
 * The event names used are "down", "up" and "clicked", "enter" and "exit"
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

    /**
     * Note, we return null, if there are no relevant events, so that Button can be optimised, so that it
     * doesn't constantly check if the mouse pointer is contained by the button (which can be slightly expensive)
     */
    override fun enterAction(button: Button): Action? {
        val event = "enter"
        if ((button.actor.costume.chooseSound(event) == null) &&
                (button.actor.costume.choosePose(event) == null) &&
                (button.actor.costume.chooseTextStyle(event) == null)) {
            return null
        } else {
            return EventAction(button.actor, event)
        }
    }

    /**
     * Note, we return null, if there are no relevant events, so that Button can be optimised, so that it
     * doesn't constantly check if the mouse pointer is contained by the button (which can be slightly expensive)
     */
    override fun exitAction(button: Button): Action? {
        val event = "exit"
        if ((button.actor.costume.chooseSound(event) == null) &&
                (button.actor.costume.choosePose(event) == null) &&
                (button.actor.costume.chooseTextStyle(event) == null)) {
            return null
        } else {
            return EventAction(button.actor, event)
        }
    }
}