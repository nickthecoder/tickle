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
import uk.co.nickthecoder.tickle.graphics.Window


class KeyInput(val key: Key, val state: ButtonState = ButtonState.PRESSED) : Input {

    override fun isPressed(): Boolean {
        return GLFW.glfwGetKey(Window.instance?.handle ?: 0, key.code) == GLFW.GLFW_PRESS
    }

    override fun matches(event: KeyEvent): Boolean {
        return event.state == state && key == event.key
    }

    override fun toString() = "KeyInput key=$key state=$state"
}
