/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.EventAction

/**
 * Sends events to the Button's Actor.
 * These events can cause the Pose to change, or make a sound effect etc.
 *
 * The event names used are "enable", "disable", "down", "up", "enter" and "exit" and "clicked".
 */
class EventButtonEffects : ButtonEffects {


    override fun enable(button: Button): Action? {
        return EventAction(button.actor, "enable")
    }

    override fun disable(button: Button): Action? {
        return EventAction(button.actor, "disable")
    }

    override fun down(button: Button): Action? {
        return EventAction(button.actor, "down")
    }

    override fun up(button: Button): Action? {
        return EventAction(button.actor, "up")
    }

    override fun clicked(button: Button): Action? {
        return EventAction(button.actor, "clicked")
    }

    /**
     * Note, we return null, if there are no relevant events, so that Button can be optimised, so that it
     * doesn't constantly check if the mouse pointer is contained by the button (which can be slightly expensive)
     */
    override fun enter(button: Button): Action? {
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
    override fun exit(button: Button): Action? {
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