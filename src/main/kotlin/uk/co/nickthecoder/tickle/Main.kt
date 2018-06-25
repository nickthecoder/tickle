package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.editor.Editor
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.sound.SoundManager
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File


/**
 * The main entry point.
 *
 * Usage : Main [--editor] [RESOURCE_FILE]
 *
 * If RESOURCE_FILE is not given, then it is found by looking for a ".tickle" file in
 * "./src/dist/resources/" or "./resources/"
 *
 * With the --editor flag, the editor is started, otherwise the game is started.
 */
fun main(args: Array<String>) {

    val file: File?
    val startEditor = args.isNotEmpty() && args[0] == "--editor"

    if (args.isEmpty() || args.size == 1 && startEditor) {
        file = guessTickleFile()
    } else {
        file = File(args.last()).absoluteFile
        if (file == null) {
            System.err.println("No tickle file found. Exiting")
            System.exit(1)
        }
    }

    if (startEditor) {
        println( "Starting editor using resources file : $file")
        file?.let { Editor.start(it) }
    } else {
        println( "Starting game using resources file : $file")
        file?.let { startGame(it) }
    }
}

fun guessTickleFile(): File? {
    // When running from dev environment, the resources are in src/dist/resources, but will be in resources
    // when running from an install application.
    val srcDist = File(File("src"), "dist")
    val resourceDir = if (srcDist.exists()) {
        File(srcDist, "resources")
    } else {
        File("resources")
    }
    return resourceDir.listFiles().filter { it.extension == "tickle" }.sortedBy { it.lastModified() }.lastOrNull()
}

fun startGame(resourcesFile: File, scenePath: String? = null) {

    // Setup an error callback.
    GLFWErrorCallback.createPrint(System.err).set()

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    val json = JsonResources(resourcesFile)
    val gameInfo = json.loadGameInfo()

    val window = Window(gameInfo.title, gameInfo.width, gameInfo.height, fullScreen = gameInfo.fullScreen)
    window.show()
    GL.createCapabilities()

    val resources = json.loadResources()
    Game(window, resources).run(scenePath ?: Resources.instance.sceneFileToPath(resources.gameInfo.initialScenePath))

    // Clean up OpenGL and OpenAL
    window.delete()
    SoundManager.cleanUp()
    GLFW.glfwTerminate()
    GLFW.glfwSetErrorCallback(null).free()
}
