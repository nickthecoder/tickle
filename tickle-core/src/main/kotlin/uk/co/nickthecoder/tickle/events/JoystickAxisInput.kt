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
import uk.co.nickthecoder.tickle.Game
import java.nio.FloatBuffer

class JoystickAxisInput(val joystickID: Int, val axis: JoystickAxis, val positive: Boolean = true, val threshold: Double = 0.5)

    : Input {

    var optimise = false

    constructor(axis: JoystickAxis, positive: Boolean, threshold: Double = 0.5)
            : this(Joystick.findFirst(), axis, positive, threshold)

    override fun isPressed(): Boolean {
        val value = axisValue(joystickID, axis, optimise)
        return if (positive) value > threshold else value < -threshold
    }

    fun value(): Float = axisValue(joystickID, axis, optimise)

    override fun matches(event: KeyEvent): Boolean {
        return false
    }

    override fun toString() = "JoystickAxisInput #$joystickID axis=$axis"

    companion object {

        private val axisHelpers = List<JoystickAxisHelper>(Joystick.count) { JoystickAxisHelper(it) }

        fun axisValue(joystickID: Int, axis: JoystickAxis, optimise: Boolean): Float {
            if (joystickID < 0 || joystickID >= Joystick.count) return 0f
            val values = axisHelpers[joystickID].readAxes(optimise)
            if (values == null) return 0f
            if (values.capacity() <= axis.axisID) return 0f
            return values[axis.axisID]
        }
    }
}

private class JoystickAxisHelper(val joystickID: Int) {

    var tick: Int = -1

    private var floatBuffer: FloatBuffer? = null

    fun readAxes(optimise: Boolean = false): FloatBuffer? {
        if (!optimise || (floatBuffer == null || tick != Game.tickCount)) {
            tick = Game.tickCount
            floatBuffer = GLFW.glfwGetJoystickAxes(joystickID)
            //println("Joy #$joystickID float buffer = ${floatBuffer}")
        }
        return floatBuffer
    }
}

enum class JoystickAxis(val axisID: Int) {
    LEFT_X(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X),
    LEFT_Y(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y),
    RIGHT_X(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X),
    RIGHT_Y(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y),
    LEFT_TRIGGER(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER),
    RIGHT_TRIGGER(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)
}
