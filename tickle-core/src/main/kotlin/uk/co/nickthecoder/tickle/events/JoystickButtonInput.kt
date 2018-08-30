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

import org.lwjgl.glfw.GLFW.*
import uk.co.nickthecoder.tickle.Game
import java.nio.ByteBuffer


class JoystickButtonInput(val joystickID: Int, val button: JoystickButton) : Input {

    var optimise: Boolean = false

    override fun isPressed(): Boolean {
        return isButtonPressed(joystickID, button, optimise)
    }

    override fun matches(event: KeyEvent): Boolean {
        return false
    }

    override fun toString() = "JoystickButtonInput #$joystickID button=$button"

    companion object {
        val joystickButtonCount: Int = GLFW_GAMEPAD_BUTTON_LAST + 1
        val pressed: Byte = GLFW_PRESS.toByte()

        private val buttonHelpers = List<JoystickButtonHelper>(Joystick.count) { JoystickButtonHelper(it) }

        fun isButtonPressed(joystickID: Int, button: JoystickButton, optimise: Boolean = false): Boolean {
            if (joystickID < 0 || joystickID >= Joystick.count) return false
            return buttonHelpers[joystickID].readButtons(optimise)?.get(button.buttonID) == pressed
        }

    }
}

private class JoystickButtonHelper(val joystickID: Int) {

    var tick: Int = -1

    private var byteBuffer: ByteBuffer? = null

    fun readButtons(optimise: Boolean = false): ByteBuffer? {
        if (!optimise || (byteBuffer == null || tick != Game.tickCount)) {
            tick = Game.tickCount
            byteBuffer = glfwGetJoystickButtons(joystickID)
            // println("Joy #$joystickID ${Joystick.isPresent(joystickID)} byte buffer = ${byteBuffer}")
        }
        return byteBuffer
    }
}

enum class JoystickButton(val buttonID: Int) {
    A(GLFW_GAMEPAD_BUTTON_A),
    B(GLFW_GAMEPAD_BUTTON_B),
    X(GLFW_GAMEPAD_BUTTON_X),
    Y(GLFW_GAMEPAD_BUTTON_Y),
    LEFT_BUMPER(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER),
    RIGHT_BUMPER(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER),
    BACK(GLFW_GAMEPAD_BUTTON_BACK),
    START(GLFW_GAMEPAD_BUTTON_START),
    GUIDE(GLFW_GAMEPAD_BUTTON_GUIDE),
    LEFT_THUMB(GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
    RIGHT_THUMB(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB),
    DPAD_UP(GLFW_GAMEPAD_BUTTON_DPAD_UP),
    DPAD_RIGHT(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT),
    DPAD_DOWN(GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
    DPAD_LEFT(GLFW_GAMEPAD_BUTTON_DPAD_LEFT)

}
