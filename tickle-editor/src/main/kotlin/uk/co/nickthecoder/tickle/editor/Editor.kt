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
package uk.co.nickthecoder.tickle.editor

import javafx.application.Application
import javafx.stage.Stage
import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.editor.resources.DesignJsonResources
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.guessTickleFile
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.sound.SoundManager
import java.io.File

class Editor : Application() {

    lateinit var window: Window

    override fun start(primaryStage: Stage) {

        AutoExit.disable()
        window = Window("Tickle Editor Hidden Window", 100, 100)
        val json = DesignJsonResources(resourceFile!!)
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
