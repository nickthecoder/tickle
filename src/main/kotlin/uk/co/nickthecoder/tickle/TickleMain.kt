package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.graphics.Window


fun main(args: Array<String>) {

    // Setup an error callback.
    GLFWErrorCallback.createPrint(System.err).set()

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    val gameInfo = GameInfo("Demo", 600, 400)
    // Create the window
    val window = Window(gameInfo.title, gameInfo.width, gameInfo.height)
    window.show()
    GL.createCapabilities()

    val resources = Resources()

    Demo(window, gameInfo, resources).run()
}
