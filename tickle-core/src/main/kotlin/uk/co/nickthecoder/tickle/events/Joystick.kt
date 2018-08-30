/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
