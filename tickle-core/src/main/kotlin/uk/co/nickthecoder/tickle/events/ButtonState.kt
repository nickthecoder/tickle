package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW

enum class ButtonState(val value: Int) {

    /**
     * Key was released
     */
    RELEASED(GLFW.GLFW_RELEASE),

    /**
     * Key was pressed
     */
    PRESSED(GLFW.GLFW_PRESS),

    /**
     * Key is held down, and the auto-repeat period has elapsed
     */
    REPEATED(GLFW.GLFW_REPEAT),

    /**
     * Unknown - should never happen!
     */
    UNKNOWN(-1);

    companion object {

        val map = mapOf(RELEASED.value to RELEASED, PRESSED.value to PRESSED, REPEATED.value to REPEATED)

        fun of(code: Int) = map[code] ?: UNKNOWN
    }
}
