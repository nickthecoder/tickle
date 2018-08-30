package uk.co.nickthecoder.tickle.events

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Window

class MouseEvent(
        val window: Window,
        val button: Int,
        val state: ButtonState,
        val mods: Int
) :Event() {

    val screenPosition = Vector2d()

    val viewPosition = Vector2d()

    internal var captured: Boolean = false

    /**
     * Capture the mouse, so that only the caller receives future mouse events until [release] is called.
     */
    fun capture() {
        captured = true
        consume()
    }

    /**
     * This is the opposite of [capture]
     */
    fun release() {
        captured = false
        consume()
    }

}
