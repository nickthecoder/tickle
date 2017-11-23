package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseHandler

abstract class Button : AbstractRole(), MouseHandler {

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
        if (event.state == ButtonState.PRESSED) {
            event.capture()
            onMousePressed(event)
        } else if (event.state == ButtonState.RELEASED) {
            event.release()
            onMouseReleased(event)
        }
    }

    override fun onMouseMove(event: MouseEvent) {
        down = actor.touching(event.viewPosition)
    }

    open fun onMousePressed(event: MouseEvent) {
        down = true
    }

    open fun onMouseReleased(event: MouseEvent) {
        if (down) {
            onMouseClicked(event)
        }
        down = false
    }

    open fun onMouseClicked(event: MouseEvent) {}

    open fun stateChanged(down: Boolean) {
        actor.event(if (down) "down" else "default")
    }

}
