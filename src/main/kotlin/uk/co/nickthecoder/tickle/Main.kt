package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File


fun main(args: Array<String>) {
    val file: File?
    if (args.isEmpty()) {
        val dir = Game.resourceDirectory
        file = dir.listFiles().filter { it.extension == "tickle" }.firstOrNull()
    } else {
        file = File(args[0]).absoluteFile
        if (file == null) {
            System.err.println("No tickle file found. Exiting")
            System.exit(1)
        }
    }

    file?.let { startGame(it) }
}

private fun startGame(file: File) {

    // Setup an error callback.
    GLFWErrorCallback.createPrint(System.err).set()

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    val window = Window("Loading", 220, 50)
    window.show()
    GL.createCapabilities()

    val resources = JsonResources(file).resources

    with(resources.gameInfo) {
        window.change(title, width, height, resizable)
    }

    println("Creating the Game")
    Game(window, resources).run()
}


