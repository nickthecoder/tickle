package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseHandler

abstract class Button : AbstractRole(), MouseHandler {

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
            down = false
            onClicked(event)
        }
    }

    open fun onClicked(event: MouseEvent) {}

    open fun stateChanged(down: Boolean) {
        actor.event(if (down) "down" else "default")
    }

}
