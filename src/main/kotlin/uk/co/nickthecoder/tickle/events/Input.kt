package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW.*
import uk.co.nickthecoder.tickle.graphics.Window

interface Input {

    fun isPressed(): Boolean

    fun matches(event: KeyEvent): Boolean

    companion object {

        val dummyInput = CompoundInput()

    }
}

class KeyInput(val key: Key, val state: ButtonState = ButtonState.PRESSED) : Input {

    override fun isPressed(): Boolean {
        return glfwGetKey(Window.current?.handle ?: 0, key.code) == GLFW_PRESS
    }

    override fun matches(event: KeyEvent): Boolean {
        return event.state == state && key == event.key
    }

    override fun toString() = "KeyInput key=$key state=$state"
}

class MouseInput(val mouseButton: Int, val state: ButtonState) : Input {

    override fun isPressed(): Boolean {
        return glfwGetMouseButton(Window.current?.handle ?: 0, mouseButton) == GLFW_PRESS
    }

    override fun matches(event: KeyEvent): Boolean {
        return false
    }

    override fun toString() = "MouseInput key=$mouseButton state=$state"
}

class CompoundInput() : Input {

    val inputs = mutableSetOf<Input>()

    fun add(input: Input) {
        inputs.add(input)
    }

    fun remove(input: Input) {
        inputs.remove(input)
    }

    override fun isPressed(): Boolean {
        return inputs.firstOrNull { it.isPressed() } != null
    }

    override fun matches(event: KeyEvent): Boolean {
        return inputs.firstOrNull { it.matches(event) } != null
    }

}

