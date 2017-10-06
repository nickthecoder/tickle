package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.graphics.Window


class MouseInput(val mouseButton: Int, val state: ButtonState) : Input {

    override fun isPressed(): Boolean {
        return GLFW.glfwGetMouseButton(Window.current?.handle ?: 0, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun matches(event: KeyEvent): Boolean {
        return false
    }

    override fun toString() = "MouseInput key=$mouseButton state=$state"
}
