package uk.co.nickthecoder.tickle.editor

import javafx.application.Application
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.graphics.WindowlessContext
import uk.co.nickthecoder.tickle.guessTickleFile
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File

class Editor() : Application() {

    var windowlessContext: WindowlessContext? = null

    override fun start(primaryStage: Stage) {

        AutoExit.disable()
        windowlessContext = WindowlessContext()
        val resources = if (resourceFile == null) Resources() else JsonResources(resourceFile!!).resources
        Resources.instance = resources

        println("Loaded resource, creating main window")
        MainWindow(primaryStage)
    }

    override fun stop() {
        println("Stopping JavaFX Application, deleting GL context")
        windowlessContext?.delete()
        windowlessContext = null
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
