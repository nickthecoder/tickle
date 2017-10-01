package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.glfwGetKey
import uk.co.nickthecoder.tickle.graphics.Window

interface Input {

    fun isPressed(): Boolean

    fun matches(event: KeyEvent): Boolean

    companion object {

        val dummyInput = CompoundInput()

    }
}

class KeyInput(val key: Key, val type: KeyEventType = KeyEventType.PRESS) : Input {

    override fun isPressed(): Boolean {
        return glfwGetKey(Window.current?.handle ?: 0, key.code) == GLFW_PRESS
    }

    override fun matches(event: KeyEvent): Boolean {
        return event.type == type && key == event.key
    }

    override fun toString() = "KeyInput key=$key type=$type"
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

