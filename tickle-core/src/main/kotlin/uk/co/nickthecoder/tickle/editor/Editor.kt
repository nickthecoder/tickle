package uk.co.nickthecoder.tickle.editor

import javafx.application.Application
import javafx.stage.Stage
import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.guessTickleFile
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.sound.SoundManager
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File

class Editor : Application() {

    lateinit var window: Window

    override fun start(primaryStage: Stage) {

        AutoExit.disable()
        window = Window("Tickle Editor Hidden Window", 100, 100)
        val json = JsonResources(resourceFile!!, editing = true)
        val resources = if (resourceFile == null) Resources() else json.loadResources()
        Game(window, resources)

        println("Loaded resource, creating main window")
        MainWindow(primaryStage, window)
    }

    override fun stop() {
        println("Stopping JavaFX Application, deleting GL context")
        window.delete()
        SoundManager.cleanUp()

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null).free()
    }

    companion object {

        var resourceFile: File? = null

        fun start(file: File? = null) {
            resourceFile = file
            Application.launch(Editor::class.java)
        }
    }
}

fun main(args: Array<String>) {

    val file: File?
    if (args.isEmpty()) {
        file = guessTickleFile()
    } else {
        file = File(args[0]).absoluteFile
        if (file == null) {
            System.err.println("No tickle file found. Exiting")
            System.exit(1)
        }
    }

    Editor.start(file)

}
