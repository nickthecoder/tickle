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
