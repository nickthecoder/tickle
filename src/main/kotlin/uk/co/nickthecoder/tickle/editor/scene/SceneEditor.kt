package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.SceneStage
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.util.background
import uk.co.nickthecoder.tickle.editor.util.isAt


class SceneEditor(val sceneResource: SceneResource)

    : SceneListerner {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    var mouseHandler: MouseHandler? = Select()

    val selection = Selection()

    val layers = Layers(sceneResource, selection)

    var dirty = true

    val shortcuts = ShortcutHelper("SceneEditor", scrollPane)

    val costumeBox = CostumesBox { selectCostumeName(it) }

    val layersBox = LayersBox(layers)

    val costumesPane = TitledPane("Costumes", costumeBox.build())
    val propertiesPane = PropertiesPane()
    val layersPane = TitledPane("Layers", layersBox.build())

    val sidePanes = listOf(costumesPane, layersPane, propertiesPane)

    val costumeHistory = mutableListOf<String>()

    init {
        sceneResource.listeners.add(this)
    }

    fun build(): Node {

        with(scrollPane) {
            content = layers.stack
        }

        with(borderPane) {
            center = scrollPane
        }

        with(layers.glass.canvas) {
            addEventHandler(MouseEvent.MOUSE_PRESSED) { onMousePressed(it) }
            addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
            addEventHandler(MouseEvent.MOUSE_DRAGGED) { onMouseDragged(it) }
            addEventHandler(MouseEvent.DRAG_DETECTED) { onDragDetected(it) }
            addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouseReleased(it) }
        }

        layers.stack.background = sceneResource.background.background()

        with(shortcuts) {
            add(EditorActions.ESCAPE) { onEscape() }
            add(EditorActions.DELETE) { onDelete() }
        }
        EditorActions.STAMPS.forEachIndexed { index, action ->
            shortcuts.add(action) { selectCostumeFromHistory(index) }
        }

        draw()
        return borderPane
    }

    fun cleanUp() {
        selection.clear() // Will clear the "Properties" box.
        sceneResource.listeners.remove(this)
    }

    override fun sceneChanged(sceneResource: SceneResource) {
        dirty = true
        Platform.runLater {
            if (dirty) {
                draw()
            }
        }
    }

    fun draw() {
        layers.draw()
        dirty = false
    }

    fun findActorsAt(x: Double, y: Double, ignoreStageLock: Boolean = false): List<SceneActor> {
        val list = mutableListOf<SceneActor>()
        layers.visibleLayers().filter { ignoreStageLock || it.isLocked == false }.forEach { stageLayer ->
            stageLayer.sceneStage.sceneActors.forEach { sceneActor ->
                if (sceneActor.isAt(x, y)) {
                    list.add(sceneActor)
                }
            }
        }
        return list
    }

    fun updateAttributesBox() {
        val latest = selection.latest()
        if (latest == null) {
            propertiesPane.clear()
            MainWindow.instance.accordion.expandedPane = costumesPane
        } else {
            propertiesPane.show(ActorProperties(latest, sceneResource))
        }
    }

    fun buildContextMenu(): ContextMenu {
        val menu = ContextMenu()
        val delete = MenuItem("Delete")
        delete.onAction = EventHandler { onDelete() }
        menu.items.add(delete)

        if (selection.isNotEmpty() && sceneResource.sceneStages.size > 1) {
            val moveToStageMenu = Menu("Move to Stage")
            sceneResource.sceneStages.forEach { stageName, stage ->
                val stageItem = MenuItem(stageName)
                stageItem.onAction = EventHandler { moveSelectTo(stage) }
                moveToStageMenu.items.add(stageItem)
            }
            menu.items.add(moveToStageMenu)
        }

        return menu
    }

    fun moveSelectTo(sceneStage: SceneStage) {
        selection.forEach { sceneActor ->
            delete(sceneActor)
            sceneStage.sceneActors.add(sceneActor)
        }
        sceneResource.fireChange()
    }

    fun onEscape() {
        mouseHandler?.escape()
        selection.clear()
        mouseHandler = Select()
        updateAttributesBox()
        draw()
    }

    fun delete(sceneActor: SceneActor) {
        sceneResource.sceneStages.values.forEach { sceneStage ->
            sceneStage.sceneActors.remove(sceneActor)
        }
    }

    fun onDelete() {
        selection.selected().forEach { sceneActor ->
            delete(sceneActor)
        }
        selection.clear()
        updateAttributesBox()
        sceneResource.fireChange()
    }


    fun selectCostumeName(costumeName: String) {
        mouseHandler = Stamp(costumeName)
        if (costumeHistory.isEmpty() || costumeHistory[0] != costumeName) {
            costumeHistory.add(0, costumeName)
            if (costumeHistory.size > 10) {
                costumeHistory.removeAt(costumeHistory.size - 1)
            }
        }
    }

    fun selectCostumeFromHistory(index: Int) {
        if (index >= 0 && index < costumeHistory.size) {
            mouseHandler = Stamp(costumeHistory[index])
        }
    }

    var dragPreviousX: Double = 0.0
    var dragPreviousY: Double = 0.0

    fun onMousePressed(event: MouseEvent) {
        dragPreviousX = event.x
        dragPreviousY = event.y

        if (event.isPopupTrigger) {

            val menu = buildContextMenu()
            menu.show(MainWindow.instance.scene.window, event.screenX, event.screenY)

        } else {
            mouseHandler?.onMousePressed(event)
        }
    }

    fun onDragDetected(event: MouseEvent) {

        mouseHandler?.onDragDetected(event)
    }

    fun onMouseMoved(event: MouseEvent) {

        mouseHandler?.onMouseMoved(event)
    }


    var dragging = false
    var dragDeltaX: Double = 0.0
    var dragDeltaY: Double = 0.0 // In the same coordinate system as event (i.e. y axis points down).

    fun onMouseDragged(event: MouseEvent) {
        dragDeltaX = event.x - dragPreviousX
        dragDeltaY = event.y - dragPreviousY

        // Prevent dragging unless the drag amount is more than a few pixels. This prevents accidental tiny movements
        if (!dragging) {
            if (Math.abs(dragDeltaX) > 5 || Math.abs(dragDeltaY) > 5) {
                dragging = true
            }
        }
        if (dragging) {

            mouseHandler?.onMouseDragged(event)
            dragPreviousX = event.x
            dragPreviousY = event.y
        }
    }

    fun onMouseReleased(event: MouseEvent) {
        mouseHandler?.onMouseReleased(event)
        dragging = false
    }

    interface MouseHandler {
        fun onMousePressed(event: MouseEvent) {
            event.consume()
        }

        fun onDragDetected(event: MouseEvent) {
            event.consume()
        }

        fun onMouseMoved(event: MouseEvent) {
            event.consume()
        }

        fun onMouseDragged(event: MouseEvent) {
            event.consume()
        }

        fun onMouseReleased(event: MouseEvent) {
            event.consume()
        }

        fun escape() {}
    }

    fun worldX(event: MouseEvent) = layers.worldX(event)
    fun worldY(event: MouseEvent) = layers.worldY(event)

    inner class Select : MouseHandler {

        override fun onMousePressed(event: MouseEvent) {
            val wx = worldX(event)
            val wy = worldY(event)

            if (event.button == MouseButton.PRIMARY) {

                val handle = layers.glass.findDragHandle(wx, wy)
                if (handle != null) {
                    mouseHandler = AdjustDragHandle(handle)

                } else {
                    val ignoreLock = event.isAltDown // On my system Mouse + Alt moves a window, but Mouse + Alt + Shift works fine ;-)
                    val actors = findActorsAt(wx, wy, ignoreLock)
                    val highestActor = actors.lastOrNull()

                    if (event.isControlDown) {
                        // Add or remove the top-most actor to/from the selection
                        if (selection.contains(highestActor)) {
                            selection.remove(highestActor)
                        } else {
                            selection.add(highestActor)
                        }
                        updateAttributesBox()

                    } else if (event.isShiftDown) {
                        if (actors.contains(selection.latest())) {
                            // Select the actor below the currently selected actor. This is useful when there are many
                            // actors on top of each other. We can get to any of them, by repeatedly shift-clicking
                            val i = actors.indexOf(selection.latest())
                            if (i == 0) {
                                selection.clearAndSelect(highestActor)
                            } else {
                                selection.clearAndSelect(actors[i - 1])
                            }

                        } else {
                            selection.clearAndSelect(highestActor)
                        }
                        updateAttributesBox()

                    } else {
                        if (actors.contains(selection.latest())) {

                            // Do nothing


                        } else if (selection.contains(highestActor)) {
                            // Already in the selection, but this makes it the "latest" one
                            // So it is shown in the details dialog, and you can see/edit its direction arrow.
                            selection.add(highestActor)
                        } else {
                            selection.clearAndSelect(highestActor)
                        }
                        updateAttributesBox()

                    }
                }

            } else if (event.button == MouseButton.MIDDLE || event.isAltDown) {
                mouseHandler = Pan()
            }
        }

        override fun onMouseMoved(event: MouseEvent) {
            layers.glass.hover(worldX(event), worldY(event))
        }

        override fun onMouseDragged(event: MouseEvent) {
            if (event.button == MouseButton.PRIMARY) {
                selection.selected().forEach { sceneActor ->
                    sceneActor.x += dragDeltaX
                    sceneActor.y -= dragDeltaY
                }
                if (selection.isNotEmpty()) {
                    sceneResource.fireChange()
                }
            }
        }

    }

    inner class Pan : MouseHandler {
        override fun onMouseDragged(event: MouseEvent) {
            layers.panBy(dragDeltaX, -dragDeltaY)
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }

    inner class AdjustDragHandle(val dragHandle: GlassLayer.DragHandle) : MouseHandler {

        override fun onMouseDragged(event: MouseEvent) {
            dragHandle.moveTo(worldX(event), worldY(event), event.isShiftDown)
            sceneResource.fireChange()
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }

    inner class Stamp(val costumeName: String) : MouseHandler {

        var newActor = SceneActor(true)

        init {
            newActor.costumeName = costumeName
            layers.glass.newActor = newActor
        }

        override fun onMouseMoved(event: MouseEvent) {
            newActor.x = worldX(event)
            newActor.y = worldY(event)
            layers.glass.dirty = true
        }

        override fun onMousePressed(event: MouseEvent) {
            layers.currentLayer()?.sceneStage?.sceneActors?.add(newActor)

            if (event.isShiftDown) {

                newActor = SceneActor()
                newActor.costumeName = costumeName
                layers.glass.newActor = newActor

            } else {
                layers.glass.newActor = null
                selection.clearAndSelect(newActor)
                mouseHandler = Select()
            }
            sceneResource.fireChange()
        }

    }
}

