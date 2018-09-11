package uk.co.nickthecoder.tickle.launcher

import javafx.application.Application
import javafx.stage.Stage
import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.groovy.GroovyLanguage
import uk.co.nickthecoder.tickle.sound.SoundManager

class Launcher : Application() {

    lateinit var window: Window

    override fun start(primaryStage: Stage?) {
        AutoExit.disable()
        window = Window("Tickle Editor Hidden Window", 100, 100)
        GroovyLanguage().register()
        LauncherWindow(primaryStage ?: Stage(), window)
    }

    override fun stop() {
        println("Stopping JavaFX Application, deleting GL context")
        window.delete()
        SoundManager.cleanUp()

        // Terminate GLFW and free the error callback
        try {
            GLFW.glfwSetErrorCallback(null).free()
            GLFW.glfwTerminate()
        } catch (e: Exception) {
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(Launcher::class.java)
}