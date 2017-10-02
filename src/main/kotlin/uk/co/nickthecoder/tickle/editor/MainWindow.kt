package uk.co.nickthecoder.tickle.editor

import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MySplitPane
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.tabs.EditorTab
import uk.co.nickthecoder.tickle.util.JsonResources

class MainWindow(val stage: Stage) {

    val borderPane = BorderPane()

    val toolBar = ToolBar()

    val splitPane = MySplitPane()

    val resourcesTree = ResourcesTree(this)

    val tabPane = MyTabPane<EditorTab>()

    val scene = Scene(borderPane, 1000.0, 600.0)

    private val shortcuts = ShortcutHelper("MainWindow", borderPane)

    init {
        stage.title = "Tickle Resources Editor"
        stage.scene = scene
        ParaTask.style(scene)

        with(borderPane) {
            top = toolBar
            center = splitPane
        }

        with(splitPane) {
            dividerRatio = 0.2
            left = resourcesTree
            right = tabPane
        }

        with(toolBar.items) {
            add(EditorActions.RESOURCES_SAVE.createButton(shortcuts) { save() })
        }

        stage.show()
        instance = this
    }

    fun findTab(data: Any): EditorTab? {
        return tabPane.tabs.firstOrNull { it.data === data }
    }

    fun save() {
        JsonResources(Resources.instance).save(Resources.instance.file)
    }

    companion object {
        var instance: MainWindow? = null
    }

}
