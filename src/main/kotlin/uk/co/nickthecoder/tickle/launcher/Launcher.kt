package uk.co.nickthecoder.tickle.launcher

import javafx.application.Application
import javafx.stage.Stage
import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.groovy.GroovyLanguage
import uk.co.nickthecoder.tickle.sound.SoundManager
import uk.co.nickthecoder.tickle.startGame
import java.io.File

class Launcher : Application() {

    lateinit var glWindow: Window

    override fun start(primaryStage: Stage?) {
        AutoExit.disable()
        glWindow = Window("Tickle Editor Hidden GL Window", 100, 100)
        GroovyLanguage().register()

        val window = LauncherWindow(primaryStage ?: Stage(), glWindow)
        resourcesFile?.let {
            window.onEdit(it)
        }
    }

    override fun stop() {
        // Terminate GLFW and free the error callback
        try {
            println("Stopping JavaFX Application, deleting GL context")
            glWindow.delete()
            SoundManager.cleanUp()
            GLFW.glfwSetErrorCallback(null).free()
            GLFW.glfwTerminate()
        } catch (e: Exception) {
            println("Stop failed $e")
        }
    }

}

private var resourcesFile: File? = null

fun main(args: Array<String>) {

    // Parse the command line arguments
    // Either no args, or a filename, or "--edit" then a filename.
    if (args.isNotEmpty()) {
        val edit = (args.size > 1 && args[0] == "--edit")
        val file = File(args[if (edit) 1 else 0])
        if (file.exists() && file.isFile) {
            if (edit) {
                resourcesFile = file
            } else {
                // Play the game without starting the JavaFX GUI.
                GroovyLanguage().register()
                startGame(file)
                return
            }
        }
    }

    Application.launch(Launcher::class.java)
}