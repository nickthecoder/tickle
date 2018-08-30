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
package uk.co.nickthecoder.tickle.sandbox

import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window


abstract class Sandbox(title: String = "Sandbox", width: Int = 800, height: Int = 400)

    : WindowListener {

    val window = Window(title, width, height)

    val renderer = Renderer(window)

    val projection = Matrix4f()

    val centerX = window.width / 2
    val centerY = window.height / 2

    init {

        GLFWErrorCallback.createPrint(System.err).set()

        if (!GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        window.show()
        GL.createCapabilities()

        window.enableVSync()
        window.wholeViewport()
        renderer.clearColor(Color.black())
        window.listeners.add(this)

        println("Projection Center @ $centerX, $centerY in window ${window.width}x${window.height}")
        projection.identity()
        projection.ortho2D(
                (centerX - window.width / 2).toFloat(), (centerX + window.width / 2).toFloat(),
                (centerY - window.height / 2).toFloat(), (centerY + window.height / 2).toFloat())

        renderer.changeProjection(projection)
    }


    fun loop() {
        while (!window.shouldClose()) {
            GLFW.glfwPollEvents()
            tick()
            window.swap()
        }
    }

    abstract fun tick()

    fun cleanUp() {

        window.delete()

        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null).free()
    }

    override fun onKey(event: KeyEvent) {
        println("Key pressed $event")
        if (event.key == Key.ESCAPE) {
            window.close()
        }
    }

    override fun onMouseButton(event: MouseEvent) {
    }

    override fun onResize(event: ResizeEvent) {
    }
}
