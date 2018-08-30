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
package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil

/**
 * Creates a GL context without displaying a window.
 * I use this in the Editor, so that the Textures etc can be loaded.
 */
class WindowlessContext {

    val handle: Long

    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if (!GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        handle = GLFW.glfwCreateWindow(10, 10, "Tickle Dummy Window", MemoryUtil.NULL, MemoryUtil.NULL)
        GLFW.glfwMakeContextCurrent(handle)

        GL.createCapabilities()

    }

    fun delete() {
        GLFW.glfwDestroyWindow(handle)
    }
}