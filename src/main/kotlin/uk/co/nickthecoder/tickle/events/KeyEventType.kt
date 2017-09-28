package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW

enum class KeyEventType(val value: Int) {

    /**
     * Key was released
     */
    RELEASE(GLFW.GLFW_RELEASE),

    /**
     * Key was pressed
     */
    PRESS(GLFW.GLFW_PRESS),

    /**
     * Key is held down, and the auto-repeat period has elapsed
     */
    REPEAT(GLFW.GLFW_REPEAT),

    /**
     * Unknown - should never happen!
     */
    UNKNOWN(-1);

    companion object {

        val map = mapOf(RELEASE.value to RELEASE, PRESS.value to PRESS, REPEAT.value to REPEAT)

        fun of(code: Int) = map[code] ?: UNKNOWN
    }
}
