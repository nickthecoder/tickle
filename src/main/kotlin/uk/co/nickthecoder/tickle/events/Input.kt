package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW.*
import uk.co.nickthecoder.tickle.graphics.Window

interface Input {

    fun isPressed(): Boolean

}

class KeyInput(val key: Int) : Input {

    override fun isPressed(): Boolean {
        return glfwGetKey(Window.current?.handle ?: 0, key) == GLFW_PRESS
    }
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
}
