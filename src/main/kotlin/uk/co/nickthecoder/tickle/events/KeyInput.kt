package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.graphics.Window


class KeyInput(val key: Key, val state: ButtonState = ButtonState.PRESSED) : Input {

    override fun isPressed(): Boolean {
        return GLFW.glfwGetKey(Window.current?.handle ?: 0, key.code) == GLFW.GLFW_PRESS
    }

    override fun matches(event: KeyEvent): Boolean {
        return event.state == state && key == event.key
    }

    override fun toString() = "KeyInput key=$key state=$state"
}
