package uk.co.nickthecoder.tickle.editor

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.tickle.Resources

class MainWindow(val stage: Stage, val resources: Resources) {

    val borderPane = BorderPane()

    val scene = Scene(borderPane, 800.0, 600.0)


    init {
        stage.title = "ParaTask"
        stage.scene = scene

        borderPane.center = Label("Hello World. There are ${resources.poses().size} poses.")
        stage.show()
    }

}
