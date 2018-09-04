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
package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.sound.SoundManager
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File

/**
 * Parses command line arguments, and then starts the game.
 * Note, in the -editor module, EditorMain subclasses this, giving more options,
 * including starting the editor, or the new game wizard.
 *
 * See [help] for command line usage information (or use --help from the command line).
 *
 * Note, I could have chosen to use Paratask to parse and run the command, but
 * I want to keep open the option of running tickle games without the JavaFX dependency
 * that paratask requires.
 */
open class Tickle(val programName: String, val args: Array<String>) {

    var resourcesFile: File? = null
    var showHelp = false
    var sceneName: String? = null
    var fullScreen: Boolean? = null
    var endOfOptions = false

    fun start() {

        var i = 0
        while (i < args.size) {
            val used = parseArg(i)
            if (used == 0) {
                println("Invalid argument '${args[i]}'\n")
                help()
                System.exit(-1)
            } else {
                i += used
            }
        }

        if (resourcesFile == null) {
            resourcesFile = guessTickleFile()
        }
        println("Resource file = $resourcesFile")
        resourcesFile?.let {
            val path = File(it.parent, "scripts")
            ScriptManager.setClasspath(path)
        }

        launch()
    }

    /**
     * @return 0 if the argument is illegal, otherwise, the number of arguments to remove.
     * e.g. a flag will return 1, and --scene will return 2 (as the next argument, the scene name,
     * must also be removed).
     */
    protected open fun parseArg(i: Int): Int {

        val arg = args[i]

        if (!endOfOptions) {
            when (arg) {
                "--" -> {
                    endOfOptions = true
                    return 1
                }
                "--help" -> {
                    showHelp = true
                    return 1
                }
                "--fullscreen" -> {
                    fullScreen = true
                    return 1
                }
                "--windowed" -> {
                    fullScreen = false
                    return 1
                }
                "--scene" -> {
                    if (i + 1 < args.size) {
                        sceneName = args[i + 1]
                        return 2
                    } else {
                        return 0
                    }
                }
            }
        }

        if (arg.startsWith("-") && !endOfOptions) {
            return 0
        } else {
            if (resourcesFile == null) {
                resourcesFile = File(arg).absoluteFile
                endOfOptions = true
                return 1
            } else {
                return 0
            }
        }
    }

    open protected fun launch() {

        if (showHelp) {
            help()

        } else {

            if (resourcesFile == null) {
                System.err.println("No '.tickle' file found in ${searchDirs.joinToString(separator = " or ")}")
                System.err.println()
                help()
                System.exit(1)
            }

            println("Starting game using resources file : $resourcesFile")
            resourcesFile?.let { startGame(it, sceneName, fullScreen) }
        }
    }

    /**
     * Prints usage information to the console.
     */
    open protected fun help() {
        helpStart()
        helpEnd()
    }

    open protected fun helpStart() {
        println("Usage : $programName [--scene SCENE_NAME] [--fullscreen] [--windowed] [RESOURCE_FILE] (Starts the game)")
        println("Or    : $programName --help (Prints these short usages instructions)")
    }

    open protected fun helpEnd() {
        println()
        println("Note. There is normally no need to specify the RESOURCE_FILE")
        println("Instead, let tickle find it automatically, which it does by looking for the first '.tickle' file in : ")
        println(searchDirs.joinToString())
        println()
    }

    val searchDirs = listOf(
            File("."),
            File(File("src"), "dist"),
            File(File(File(programName), "src"), "dist"))
            .map { File(it, "resources") }

    /**
     * Looks for a file with an extension of '.tickle' within "./src/dist/resources" or "./resources".
     */
    fun guessTickleFile(): File? {

        for (dir in searchDirs) {
            val file = dir.listFiles()?.filter { it.extension == "tickle" }?.sortedBy { it.lastModified() }?.firstOrNull()
            if (file != null) {
                return file
            }
        }
        return null
    }
}

/**
 * Starts the game (not the editor).
 */
fun startGame(resourcesFile: File, scenePath: String? = null, fullScreen: Boolean? = null) {

    // Setup an error callback.
    GLFWErrorCallback.createPrint(System.err).set()

    if (!GLFW.glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    val json = JsonResources(resourcesFile)
    val gameInfo = json.loadGameInfo()

    val window = Window(gameInfo.title, gameInfo.width, gameInfo.height, fullScreen = if (fullScreen == null) gameInfo.fullScreen else fullScreen)
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
