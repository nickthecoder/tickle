package uk.co.nickthecoder.tickle.events

import org.lwjgl.glfw.GLFW

object Joystick {

    val count: Int = GLFW.GLFW_JOYSTICK_LAST

    fun isPresent(joystickID: Int): Boolean = GLFW.glfwJoystickPresent(joystickID)

    fun findFirst(): Int {
        for (i in 0..count) {
            if (isPresent(i)) {
                return i
            }
        }
        return -1
    }

    fun debug() {
        var found = false
        for (i in 0..Joystick.count) {
            if (Joystick.isPresent(i)) {
                found = true
                print("Stick #$i : ")

                JoystickButton.values().forEach { joystickButton ->
                    if (JoystickButtonInput(i, joystickButton).isPressed()) {
                        print("Button $joystickButton ")
                    }
                }
                JoystickAxis.values().forEach { joystickAxis ->
                    val value = JoystickAxisInput(i, joystickAxis).value()
                    if (value > 0.1 || value < -0.1) {
                        print("Axis $joystickAxis value = $value ")
                    }
                }
            }
        }
        if (found) {
            println()
        } else {
            println("No joysticks found")
        }
    }
}

fun main(args: Array<String>) {

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    while (true) {
        Joystick.debug()
        Thread.sleep(1000)
    }
}
