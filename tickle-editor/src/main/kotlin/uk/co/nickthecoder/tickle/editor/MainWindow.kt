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

import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import javafx.stage.WindowEvent
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.tedi.TediArea
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.resources.DesignJsonResources
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.editor.tabs.*
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.editor.util.NewResourceTask
import uk.co.nickthecoder.tickle.events.CompoundInput
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.resources.FontResource
import uk.co.nickthecoder.tickle.resources.Layout
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.SceneStub
import uk.co.nickthecoder.tickle.scripts.ScriptException
import uk.co.nickthecoder.tickle.scripts.ScriptManager
import uk.co.nickthecoder.tickle.sound.Sound
import uk.co.nickthecoder.tickle.util.ErrorHandler


class MainWindow(val stage: Stage, val glWindow: Window) : ErrorHandler {

    val borderPane = BorderPane()

    val toolBar = ToolBar()

    val splitPane = SplitPane()

    val resourcesTree = ResourcesTree()

    val accordion = Accordion()

    val resourcesPane = TitledPane("Resources", resourcesTree)

    val tabPane = TabPane()

    val scene = Scene(borderPane, resourcesTree.resources.preferences.windowWidth, resourcesTree.resources.preferences.windowHeight)

    private var extraSidePanels: Collection<TitledPane> = emptyList()
    private var extraButtons: Collection<Node> = emptyList()
    private var extraShortcuts: ShortcutHelper? = null

    private val shortcuts = ShortcutHelper("MainWindow", borderPane)


    init {

        stage.title = "Tickle Resources Editor"
        stage.scene = scene
        ParaTask.style(scene)
        TediArea.style(scene)

        val resource = MainWindow::class.java.getResource("tickle.css")
        scene.stylesheets.add(resource.toExternalForm())

        with(borderPane) {
            top = toolBar
            center = splitPane
        }

        with(splitPane) {
            setDividerPositions(0.2)
            items.addAll(accordion, tabPane)
        }

        // I like the animation, but it's too slow, and there is no API to change the speed. Turn it off. Grr.
        resourcesPane.isAnimated = false

        with(accordion) {
            panes.addAll(resourcesPane)
            expandedPane = resourcesPane
        }

        val toolBarPadding = HBox()
        HBox.setHgrow(toolBarPadding, Priority.ALWAYS)
        with(toolBar.items) {
            add(EditorActions.RELOAD.createButton(shortcuts) { reload() })
            add(EditorActions.NEW.createButton(shortcuts) { newResource() })
            add(EditorActions.RUN.createButton(shortcuts) { startGame() })
            add(EditorActions.TEST.createButton(shortcuts) { testGame() })
            if (ScriptManager.languages().isNotEmpty()) {
                add(EditorActions.RELOAD_SCRIPTS.createButton(shortcuts) { ScriptManager.reloadAll() })
            }
            add(toolBarPadding)
        }

        with(shortcuts) {
            add(EditorActions.ACCORDION_ONE) { accordionPane(0) }
            add(EditorActions.ACCORDION_TWO) { accordionPane(1) }
            add(EditorActions.ACCORDION_THREE) { accordionPane(2) }
            add(EditorActions.ACCORDION_FOUR) { accordionPane(3) }
            add(EditorActions.ACCORDION_FIVE) { accordionPane(4) }
            add(EditorActions.TAB_CLOSE) { onCloseTab() }

            add(EditorActions.SHOW_COSTUME_PICKER) { accordionPane(1) }

        }

        tabPane.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            onTabChanged(newValue)
        }
        tabPane.tabs.addListener(ListChangeListener<Tab> { c ->
            while (c.next()) {
                if (c.wasRemoved()) {
                    c.removed.filterIsInstance<EditorTab>().forEach { it.removed() }
                }
            }
        })

        if (resourcesTree.resources.preferences.isMaximized) {
            stage.isMaximized = true
        }
        stage.show()
        instance = this
        stage.onCloseRequest = EventHandler<WindowEvent> { onCloseRequest(it) }

        ErrorHandler.errorHandler = this

    }

    fun onCloseTab() {
        val selectedTab = tabPane.selectionModel.selectedItem
        if (selectedTab is EditTab) {

            if (selectedTab.needsSaving) {
                val alert = Alert(Alert.AlertType.CONFIRMATION)
                with(alert) {
                    headerText = "Close tab without saving?"
                    showAndWait()
                    if (result == ButtonType.OK) {
                        selectedTab.close()
                    }
                }
                return
            }
        }
        (selectedTab as? EditTab)?.close()
    }

    fun onCloseRequest(event: WindowEvent) {
        // Check if there are tabs open, and if so, ask if they should be saved.
        if (tabPane.tabs.filterIsInstance<EditTab>().filter { it.needsSaving }.isNotEmpty()) {
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.title = "Save Changes?"
            alert.contentText = "Save changes before closing?"
            val save = ButtonType("Save")
            val ignore = ButtonType("Ignore")
            val cancel = ButtonType("Cancel", ButtonData.CANCEL_CLOSE)
            alert.buttonTypes.setAll(save, ignore, cancel)

            when (alert.showAndWait().get()) {
                save -> {
                    // Note. this is inefficient, as the resources are saved to disk for every opened tab.
                    // It is quick though, so I haven't bothered to make it save only once.
                    tabPane.tabs.forEach { tab ->
                        if (tab is EditTab) {
                            if (!tab.save()) {
                                tabPane.selectionModel.selectedItem === tab
                                // Tab not saved, so abort the closing of the main window.
                                event.consume()
                                return
                            }
                        }
                    }
                }
                cancel -> {
                    event.consume()
                    return
                }
            }
        }

        with(resourcesTree.resources.preferences) {
            isMaximized = stage.isMaximized
            if (!isMaximized) {
                windowWidth = scene.width
                windowHeight = scene.height
            }
        }
        resourcesTree.resources.save()
    }

    fun accordionPane(n: Int) {
        if (n >= 0 && n < accordion.panes.count()) {
            accordion.expandedPane = accordion.panes[n]
        }
    }

    fun findTab(data: Any): EditorTab? {
        return tabPane.tabs.filterIsInstance<EditorTab>().firstOrNull { it.data == data }
    }

    fun reload() {
        saveAllTabs()
        tabPane.tabs.clear()
        Resources.instance.destroy()
        DesignJsonResources(Resources.instance.file).loadResources()
        ImageCache.clear()
        resourcesTree.reload()
    }

    fun newResource() {
        TaskPrompter(NewResourceTask()).placeOnStage(Stage())
    }

    fun saveAllTabs() {
        tabPane.tabs.forEach { tab ->
            (tab as? EditTab)?.save()
        }
    }

    fun startGame(scenePath: String = Resources.instance.sceneFileToPath(Resources.instance.gameInfo.initialScenePath)) {

        saveAllTabs()
        ScriptManager.reloadAll()

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
        val tab = tabPane.selectionModel.selectedItem
        if (tab is SceneTab) {
            startGame(Resources.instance.sceneFileToPath(tab.sceneFile))
        } else {
            startGame(Resources.instance.sceneFileToPath(Resources.instance.gameInfo.testScenePath))
        }
    }

    fun openTab(tab: EditorTab) {
        tabPane.tabs.add(tab)
        tabPane.selectionModel.select(tab)
    }

    fun openTab(dataName: String, data: Any): Tab? {

        val tab = findTab(data)
        if (tab == null) {
            val newTab = createTab(dataName, data)
            newTab?.let { openTab(it) }
            return newTab
        } else {
            tabPane.selectionModel.select(tab)
            return tab
        }

    }


    fun openNamedTab(dataName: String, type: ResourceType): Tab? {
        val data: Any = type.findResource(dataName) ?: return null
        return openTab(dataName, data)
    }

    private fun createTab(name: String, data: Any): EditorTab? {

        return when (data) {
            is GameInfo -> return GameInfoTab()

            is EditorPreferences -> EditorPreferencesTab()
            is Texture -> TextureTab(name, data)
            is Pose -> PoseTab(name, data)
            is Layout -> LayoutTab(name, data)
            is CompoundInput -> InputTab(name, data)
            is Costume -> CostumeTab(name, data)
            is CostumeGroup -> CostumeGroupTab(name, data)
            is SceneStub -> SceneTab(name, data)
            is FontResource -> FontTab(name, data)
            is Sound -> SoundTab(name, data)
            is ScriptStub -> ScriptTab(data)
            is FXCoderStub -> FXCoderTab(data)
            APIStub -> APITab()
            else -> null
        }
    }

    fun onTabChanged(tab: Tab?) {
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

    override fun handleError(e: Exception) {
        if (e is ScriptException) {
            e.file?.let { file ->
                val tab = openTab(file.nameWithoutExtension, ScriptStub(file))
                if (tab is ScriptTab) { // It should be!
                    tab.highlightError(e)
                    return
                }
            }
        }
        // TODO Handle other errors.
        println("MainWindow isn't handling this well!")
        e.printStackTrace()
    }

    companion object {
        lateinit var instance: MainWindow
    }

}
