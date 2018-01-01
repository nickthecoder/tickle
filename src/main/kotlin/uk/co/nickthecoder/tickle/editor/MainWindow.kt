package uk.co.nickthecoder.tickle.editor

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Accordion
import javafx.scene.control.TitledPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.MySplitPane
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.tabs.*
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.editor.util.NewResourceTask
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Layout
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.util.JsonResources

class MainWindow(val stage: Stage, val glWindow: Window) {

    val borderPane = BorderPane()

    val toolBar = ToolBar()

    val splitPane = MySplitPane()

    val resourcesTree = ResourcesTree()

    val accordion = Accordion()

    val resourcesPane = TitledPane("Resources", resourcesTree)

    val tabPane = MyTabPane<EditorTab>()

    val scene = Scene(borderPane, 1000.0, 650.0)

    private var extraSidePanels: Collection<TitledPane> = emptyList()
    private var extraButtons: Collection<Node> = emptyList()
    private var extraShortcuts: ShortcutHelper? = null

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

        with(accordion) {
            panes.addAll(resourcesPane)
            expandedPane = resourcesPane
        }

        val toolBarPadding = HBox()
        HBox.setHgrow(toolBarPadding, Priority.ALWAYS);
        with(toolBar.items) {
            add(EditorActions.RESOURCES_SAVE.createButton(shortcuts) { save() })
            add(EditorActions.RELOAD.createButton(shortcuts) { reload() })
            add(EditorActions.NEW.createButton(shortcuts) { newResource() })
            add(EditorActions.RUN.createButton(shortcuts) { startGame() })
            add(EditorActions.TEST.createButton(shortcuts) { testGame() })
            add(EditorActions.FXCODER.createButton(shortcuts) { fxcoder() })
            add(toolBarPadding)
        }

        with(shortcuts) {
            add(EditorActions.ACCORDION_ONE) { accordionPane(0) }
            add(EditorActions.ACCORDION_TWO) { accordionPane(1) }
            add(EditorActions.ACCORDION_THREE) { accordionPane(2) }
            add(EditorActions.ACCORDION_FOUR) { accordionPane(3) }
            add(EditorActions.ACCORDION_FIVE) { accordionPane(4) }
            add(EditorActions.TAB_CLOSE) { tabPane.selectedTab?.close() }

            add(EditorActions.SHOW_COSTUME_PICKER) { accordionPane(1) }

        }

        tabPane.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            onTabChanged(newValue)
        }


        val resource = MainWindow::class.java.getResource("tickle.css")
        scene.stylesheets.add(resource.toExternalForm())

        stage.show()
        instance = this
    }

    fun accordionPane(n: Int) {
        if (n >= 0 && n < accordion.panes.count()) {
            accordion.expandedPane = accordion.panes[n]
        }
    }

    fun findTab(data: Any): EditorTab? {
        return tabPane.tabs.firstOrNull { it.data == data }
    }

    fun save() {
        tabPane.tabs.forEach { tab ->
            if (tab is EditTab) {
                if (!tab.save()) {
                    tab.isSelected = true
                }
            }
        }
        JsonResources(Resources.instance).save(Resources.instance.file)
    }

    fun reload() {
        Resources.instance.reload()
        ImageCache.clear()
    }

    fun newResource() {
        TaskPrompter(NewResourceTask()).placeOnStage(Stage())
    }

    fun startGame(scenePath: String = Resources.instance.sceneFileToPath(Resources.instance.gameInfo.initialScenePath)) {
        stage.hide()

        // Give this window the opportunity to hide before the UI hangs
        Platform.runLater {
            println("Game test started")
            with(Resources.instance.gameInfo) {
                glWindow.change(title, width, height, resizable)
            }
            glWindow.show()

            // Clean up the old Game instance, and create a new one.
            Game.instance.cleanUp()
            Game(glWindow, Resources.instance).run(scenePath)

            println("Game test ended")
            glWindow.hide()

            stage.show()
        }
    }


    fun testGame() {
        startGame(Resources.instance.sceneFileToPath(Resources.instance.gameInfo.testScenePath))
    }

    fun fxcoder() {
        FXCoder(Stage())
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

        } else if (data is EditorPreferences) {
            return EditorPreferencesTab()

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

        } else if (data is CostumeGroup) {
            return CostumeGroupTab(name, data)

        } else if (data is SceneStub) {
            return SceneTab(name, data)

        } else if (data is FontResource) {
            return FontTab(name, data)

        } else if (data is Sound) {
            return SoundTab(name, data)
        }

        return null
    }

    fun onTabChanged(tab: EditorTab?) {
        extraSidePanels.forEach {
            accordion.panes.remove(it)
        }
        extraButtons.forEach {
            toolBar.items.remove(it)
        }
        extraShortcuts?.disable()

        if (tab is HasExtras) {
            extraSidePanels = tab.extraSidePanes()
            extraSidePanels.forEach {
                it.isAnimated = false
                accordion.panes.add(it)
            }

            extraButtons = tab.extraButtons()
            extraButtons.forEach {
                toolBar.items.add(it)
            }

            extraShortcuts = tab.extraShortcuts()
            extraShortcuts?.enable()
        }

        accordion.expandedPane = resourcesPane
    }

    companion object {
        lateinit var instance: MainWindow
    }

}
