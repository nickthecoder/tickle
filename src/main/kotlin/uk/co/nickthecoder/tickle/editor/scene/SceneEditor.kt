package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.util.background
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.Adjustment
import uk.co.nickthecoder.tickle.resources.ModificationType
import uk.co.nickthecoder.tickle.resources.SceneResource


class SceneEditor(val sceneResource: SceneResource) {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    var mouseHandler: MouseHandler? = Select()

    val selection = Selection()

    val layers = Layers(sceneResource, selection)

    val shortcuts = ShortcutHelper("SceneEditor", MainWindow.instance.borderPane)

    val costumeBox = CostumePickerBox { selectCostumeName(it) }
    val layersBox = LayersBox(layers)
    val actorsBox = ActorsBox(this)

    val costumesPane = TitledPane("Costume Picker", costumeBox.build())
    val attributesPane = TitledPane("Attributes", ActorAttributesBox(this).build())
    val stagesPane = TitledPane("Actors", actorsBox.build())
    val layersPane = TitledPane("Layers", layersBox.build())

    // NOTE. If this order changes, also change the index for SHOW_COSTUME_PICKER in MainWindow
    val sidePanes = listOf(costumesPane, attributesPane, stagesPane, layersPane)

    val costumeHistory = mutableListOf<String>()

    val snapToGridButton = EditorActions.SNAP_TO_GRID_EDIT.createButton(shortcuts) { sceneResource.snapToGrid.edit() }
    val snapToGuidesButton = EditorActions.SNAP_TO_GUIDES_EDIT.createButton(shortcuts) { sceneResource.snapToGuides.edit() }
    val snapToOthersButton = EditorActions.SNAP_TO_OTHERS_EDIT.createButton(shortcuts) { sceneResource.snapToOthers.edit() }
    val snapRotationButton = EditorActions.SNAP_ROTATION_EDIT.createButton(shortcuts) { sceneResource.snapRotation.edit() }

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
            add(EditorActions.ZOOM_RESET) { layers.scale = 1.0 }
            add(EditorActions.ZOOM_IN1) { layers.scale *= 1.2 }
            add(EditorActions.ZOOM_IN2) { layers.scale *= 1.2 }
            add(EditorActions.ZOOM_OUT) { layers.scale /= 1.2 }
            add(EditorActions.SNAP_TO_GRID_TOGGLE) { sceneResource.snapToGrid.enabled = !sceneResource.snapToGrid.enabled }
            add(EditorActions.SNAP_TO_GUIDES_TOGGLE) { sceneResource.snapToGuides.enabled = !sceneResource.snapToGuides.enabled }
            add(EditorActions.SNAP_TO_OTHERS_TOGGLE) { sceneResource.snapToOthers.enabled = !sceneResource.snapToOthers.enabled }
            add(EditorActions.SNAP_ROTATION_TOGGLE) { sceneResource.snapRotation.enabled = !sceneResource.snapRotation.enabled }
        }

        EditorActions.STAMPS.forEachIndexed { index, action ->
            shortcuts.add(action) { selectCostumeFromHistory(index) }
        }

        layers.visibleLayers().forEach { it.draw() }
        layers.glass.draw()

        return borderPane
    }

    fun cleanUp() {
        selection.clear() // Will clear the "Properties" box.
        shortcuts.clear()
    }


    fun findActorsAt(x: Double, y: Double, ignoreStageLock: Boolean = false): List<ActorResource> {
        val list = mutableListOf<ActorResource>()
        layers.visibleLayers().filter { ignoreStageLock || !it.isLocked }.forEach { stageLayer ->
            list.addAll(stageLayer.actorsAt(x, y))
        }
        return list
    }

    fun buildContextMenu(): ContextMenu {
        val menu = ContextMenu()

        if (selection.isNotEmpty()) {

            val deleteText = "Delete " + if (selection.size > 1) {
                "(${selection.size} actors)"
            } else {
                selection.latest()!!.costumeName
            }

            val delete = MenuItem(deleteText)
            delete.onAction = EventHandler { onDelete() }

            val attributes = MenuItem("Attributes")
            attributes.onAction = EventHandler { MainWindow.instance.accordion.expandedPane = attributesPane }

            menu.items.addAll(delete, attributes)

            if (sceneResource.stageResources.size > 1) {
                val moveToStageMenu = Menu("Move to Stage")
                layers.stageLayers().forEach { stageLayer ->
                    val stageItem = CheckMenuItem(stageLayer.stageName)
                    stageItem.onAction = EventHandler { moveSelectTo(stageLayer) }
                    moveToStageMenu.items.add(stageItem)

                    // Add a tick if ALL of the selected ActorResources are on this layer.
                    if (selection.filter { it.layer !== stageLayer }.isEmpty()) {
                        stageItem.isSelected = true
                    }
                }
                menu.items.add(moveToStageMenu)
            }
            menu.items.addAll(SeparatorMenuItem())
        }

        val resetAllZOrders = EditorActions.RESET_ZORDERS.createMenuItem { resetZOrders() }
        val snapToGrid = EditorActions.SNAP_TO_GRID_TOGGLE.createCheckMenuItem(property = sceneResource.snapToGrid::enabled) {}
        val snapToGuides = EditorActions.SNAP_TO_GUIDES_TOGGLE.createCheckMenuItem(property = sceneResource.snapToGuides::enabled) {}
        val snapToOthers = EditorActions.SNAP_TO_OTHERS_TOGGLE.createCheckMenuItem(property = sceneResource.snapToOthers::enabled) {}
        val snapRotation = EditorActions.SNAP_ROTATION_TOGGLE.createCheckMenuItem(property = sceneResource.snapRotation::enabled) {}

        menu.items.addAll(resetAllZOrders, SeparatorMenuItem(), snapToGrid, snapToGuides, snapToOthers, snapRotation)

        return menu
    }

    fun moveSelectTo(layer: StageLayer) {
        selection.forEach { actorResource ->
            delete(actorResource)
            layer.stageResource.actorResources.add(actorResource)
            actorResource.layer = layer
            sceneResource.fireChange(actorResource, ModificationType.NEW)
        }
    }

    fun onEscape() {
        mouseHandler?.escape()
        selection.clear()
        mouseHandler = Select()
    }

    fun delete(actorResource: ActorResource) {
        actorResource.layer?.stageResource?.actorResources?.remove(actorResource)
        sceneResource.fireChange(actorResource, ModificationType.DELETE)
    }

    fun onDelete() {
        selection.selected().forEach { actorResource ->
            delete(actorResource)
        }
        selection.clear()
    }

    fun resetZOrders() {
        sceneResource.stageResources.forEach { _, stageResource ->
            stageResource.actorResources.forEach { ar ->
                ar.costume()?.let {
                    ar.zOrder = it.zOrder
                }
            }
        }
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

    private val adjustments = mutableListOf<Adjustment>()

    fun snapActor(actorResource: ActorResource, isNew: Boolean, useSnaps: Boolean) {

        if (actorResource.layer?.stageConstraint?.snapActor(actorResource, isNew) != true) {

            if (useSnaps) {
                adjustments.clear()

                sceneResource.snapToGrid.snapActor(actorResource, adjustments)
                sceneResource.snapToGuides.snapActor(actorResource, adjustments)
                sceneResource.snapToOthers.snapActor(actorResource, adjustments)
                if (adjustments.isNotEmpty()) {
                    adjustments.sortBy { it.score }
                    actorResource.x += adjustments.first().x
                    actorResource.y += adjustments.first().y
                }
            }
        }
    }

    fun viewX(event: MouseEvent) = layers.viewX(event)

    fun viewY(event: MouseEvent) = layers.viewY(event)

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

    inner class Select : MouseHandler {

        var offsetX: Double = 0.0
        var offsetY: Double = 0.0

        override fun onMousePressed(event: MouseEvent) {
            val wx = viewX(event)
            val wy = viewY(event)

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

                    } else {
                        if (event.clickCount == 2) {

                            MainWindow.instance.accordion.expandedPane = attributesPane

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
                        }
                    }
                }
                selection.latest()?.let { latest ->
                    offsetX = latest.x - wx
                    offsetY = latest.y - wy
                }

            } else if (event.button == MouseButton.MIDDLE || event.isAltDown) {
                mouseHandler = Pan()
            }
        }

        override fun onMouseMoved(event: MouseEvent) {
            layers.glass.hover(viewX(event), viewY(event))
        }

        override fun onMouseDragged(event: MouseEvent) {
            if (event.button == MouseButton.PRIMARY) {
                selection.selected().forEach { actorResource ->
                    actorResource.draggedX += dragDeltaX
                    actorResource.draggedY -= dragDeltaY
                    snapActor(actorResource, false, !event.isControlDown)
                    sceneResource.fireChange(actorResource, ModificationType.CHANGE)
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
            dragHandle.moveTo(viewX(event), viewY(event), !event.isControlDown)
            selection.latest()?.let { sceneResource.fireChange(it, ModificationType.CHANGE) }
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }

    inner class Stamp(val costumeName: String) : MouseHandler {

        var newActor = ActorResource(true)

        init {
            newActor.costumeName = costumeName
            newActor.layer = layers.currentLayer()
            layers.glass.newActor = newActor
        }

        override fun onMouseMoved(event: MouseEvent) {

            newActor.draggedX = viewX(event)
            newActor.draggedY = viewY(event)
            snapActor(newActor, true, !event.isControlDown)
            layers.glass.dirty = true
        }

        override fun onMousePressed(event: MouseEvent) {

            if (layers.currentLayer()?.stageConstraint?.addActorResource(newActor) == true) {
                layers.currentLayer()?.stageResource?.actorResources?.add(newActor)
                newActor.layer = layers.currentLayer()

                if (event.isShiftDown) {

                    newActor = ActorResource()
                    newActor.costumeName = costumeName
                    layers.glass.newActor = newActor

                } else {
                    layers.glass.newActor = null
                    selection.clearAndSelect(newActor)
                    mouseHandler = Select()
                }
                sceneResource.fireChange(newActor, ModificationType.NEW)

            }

        }

    }
}

