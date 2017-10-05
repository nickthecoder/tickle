package uk.co.nickthecoder.tickle.editor

import javafx.scene.Scene
import javafx.scene.control.Accordion
import javafx.scene.control.Alert
import javafx.scene.control.TitledPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MySplitPane
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.tabs.*
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.JsonResources
import java.io.File

class MainWindow(val stage: Stage) {

    val borderPane = BorderPane()

    val toolBar = ToolBar()

    val splitPane = MySplitPane()

    val resourcesTree = ResourcesTree()

    val accordion = Accordion()

    val resourcesPane = TitledPane("Resources", resourcesTree)
    val costumeBox = CostumesBox()
    val costumesPane = TitledPane("Costumes", costumeBox.build())
    val propertiesPane = PropertiesPane()

    val tabPane = MyTabPane<EditorTab>()

    val scene = Scene(borderPane, 1000.0, 650.0)

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
            left = accordion
            right = tabPane
        }

        // I like the animation, but it's too slow, and there is no API to change the speed. Turn it off. Grr.
        resourcesPane.isAnimated = false
        costumesPane.isAnimated = false
        propertiesPane.isAnimated = false

        with(accordion) {
            panes.addAll(resourcesPane, costumesPane, propertiesPane)
            expandedPane = resourcesPane
        }

        with(toolBar.items) {
            add(EditorActions.RESOURCES_SAVE.createButton(shortcuts) { save() })
            add(EditorActions.NEW.createButton(shortcuts) { newResource() })
            add(EditorActions.RUN.createButton(shortcuts) { startGame() })
        }

        with(shortcuts) {
            add(EditorActions.ACCORDION_RESOURCES) { accordion.expandedPane = resourcesPane }
            add(EditorActions.ACCORDION_COSTUME) { accordion.expandedPane = costumesPane }
            add(EditorActions.ACCORDION_PROPERTIES) { accordion.expandedPane = propertiesPane }
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

    fun newResource() {
        TaskPrompter(NewResourceTask()).placeOnStage(Stage())
    }

    var running: Boolean = false

    fun startGame(sceneFile: File = Resources.instance.initialSceneFile) {
        if (running) {
            Alert(Alert.AlertType.INFORMATION, "It seems that a game is already running.\nYou can only run one instance!").showAndWait()
        } else {
            Thread {
                running = true
                println("Game test started")
                startGame(Resources.instance.file, sceneFile)
                println("Game test ended")
                running = false
            }.start()
        }
    }

    fun openTab(dataName: String, data: Any) {

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

        } else if (data is Texture) {
            return TextureTab(name, data)

        } else if (data is Pose) {
            return PoseTab(name, data)

        } else if (data is Layout) {
            return LayoutTab(name, data)

        } else if (data is CompoundInput) {
            return InputTab(name, data)

        } else if (data is Costume) {
            return CostumeTab(name, data)

        } else if (data is SceneStub) {
            return SceneTab(name, data)
        }

        return null
    }

    fun selectCostume(costume: Costume) {
        tabPane.selectionModel.selectedItem?.let { tab ->
            if (tab is SceneTab) {
                tab.selectCostume(costume)
            }
        }
    }

    companion object {
        var instance: MainWindow? = null
    }

}
