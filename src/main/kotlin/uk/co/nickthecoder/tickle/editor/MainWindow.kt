package uk.co.nickthecoder.tickle.editor

import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MySplitPane
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.tabs.*
import uk.co.nickthecoder.tickle.events.CompoundInput
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

    fun openTab( dataName : String, data : Any ) {

        val tab = findTab(data)
        if (tab == null) {
            val newTab = createTab(dataName, data)
            if (newTab != null) {
                tabPane.add(newTab)
                newTab.isSelected = true
            }
        } else {
            tab.isSelected = true
        }
    }

    fun createTab(name: String, data: Any): EditorTab? {

        if (data is GameInfo) {
            return GameInfoTab()

        } else if (data is TextureResource) {
            return TextureTab(name, data)

        } else if (data is Pose) {
            return PoseTab(name, data)

        } else if (data is Layout) {
            return LayoutTab(name, data)

        } else if (data is CompoundInput) {
            return InputTab(name, data)

        } else if (data is Costume) {
            return CostumeTab(name, data)
        }

        return null
    }

    companion object {
        var instance: MainWindow? = null
    }

}
