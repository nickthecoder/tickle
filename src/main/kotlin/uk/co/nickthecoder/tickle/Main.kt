package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File


/**
 * The main entry point to run a game.
 * With no arguments, the resources files is found by looking for any ".tickle" file in
 * "./src/dist/resources/" or "./resources/"
 *
 * However, if you do not want this automatic behaviour, you can specify a file as the first argument.
 */
fun main(args: Array<String>) {
    val file: File?
    if (args.isEmpty()) {
        // When running from dev environment, the resources are in src/dist/resources, but will be in resources
        // when running from an install application.
        val srcDist = File(File("src"), "dist")
        val resourceDir = if (srcDist.exists()) {
            File(srcDist, "resources")
        } else {
            File("resources")
        }
        file = resourceDir.listFiles().filter { it.extension == "tickle" }.firstOrNull()
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

    Game(window, resources).run()
}


