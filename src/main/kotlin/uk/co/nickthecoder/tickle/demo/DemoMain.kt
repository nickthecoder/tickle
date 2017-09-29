package uk.co.nickthecoder.tickle.demo

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File


fun main(args: Array<String>) {

    // Setup an error callback.
    GLFWErrorCallback.createPrint(System.err).set()

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    val window = Window("Loading", 220, 50)
    window.show()
    GL.createCapabilities()

    val resources = JsonResources(File(Game.resourceDirectory, "demo.tickle")).resources

    with(resources.gameInfo) {
        window.change(title, width, height, resizable)
    }

    println("Creating the Game")
    Game(window, resources).run()
}
