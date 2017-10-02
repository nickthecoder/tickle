package uk.co.nickthecoder.tickle.editor

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.tickle.editor.tabs.EditorTab
import uk.co.nickthecoder.tickle.editor.tabs.GameInfoTab

class MainWindow(val stage: Stage) {

    val borderPane = BorderPane()

    val resourcesTree = ResourcesTree(this)

    val tabPane = MyTabPane<EditorTab>()

    val scene = Scene(borderPane, 1000.0, 600.0)


    init {
        stage.title = "ParaTask"
        stage.scene = scene
        ParaTask.style(scene)

        borderPane.left = resourcesTree
        borderPane.center = tabPane
        stage.show()

        tabPane.add(GameInfoTab())
    }

    fun findTab(data: Any): EditorTab? {
        return tabPane.tabs.firstOrNull { it.data === data }
    }

}
