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

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseListener

abstract class Button : AbstractRole(), MouseListener {

    var enabled: Boolean = true
        set(v) {
            field = v
            actor.event(if (v) "default" else "disable")
        }

    var down: Boolean = false
        set(v) {
            if (v != field) {
                field = v
                stateChanged(v)
            }
        }

    override fun tick() {
    }

    override fun onMouseButton(event: MouseEvent) {
        if (enabled) {
            if (event.state == ButtonState.PRESSED) {
                event.capture()
                onPressed(event)
            } else if (event.state == ButtonState.RELEASED) {
                event.release()
                onReleased(event)
            }
        }
    }

    override fun onMouseMove(event: MouseEvent) {
        down = actor.touching(event.viewPosition)
    }

    open fun onPressed(event: MouseEvent) {
        down = true
    }

    open fun onReleased(event: MouseEvent) {
        if (down) {
            onClicked(event)
        }
        down = false
    }

    open fun onClicked(event: MouseEvent) {}

    open fun stateChanged(down: Boolean) {
        actor.event(if (down) "down" else "default")
    }

}
