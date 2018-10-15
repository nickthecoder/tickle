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
package uk.co.nickthecoder.tickle.launcher

import javafx.application.Application
import javafx.stage.Stage
import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.tickle.Tickle
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.groovy.GroovyLanguage
import uk.co.nickthecoder.tickle.sound.SoundManager
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
                Tickle.startGame(file)
                return
            }
        }
    }

    Application.launch(Launcher::class.java)
}