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
package uk.co.nickthecoder.tickle.editor.scene

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.DesignSceneResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.scene.history.*
import uk.co.nickthecoder.tickle.editor.util.background

class SceneEditor(val sceneResource: DesignSceneResource) {

    /**
     * Stores the changes made to the scene to allow undo/redo.
     */
    val history = History(this)

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    var mouseHandler: MouseHandler? = Select()
        set(v) {
            field?.end()
            field = v
        }

    val selection = Selection()

    val layers = Layers(this)

    val shortcuts = ShortcutHelper("SceneEditor", MainWindow.instance.borderPane)

    val costumeBox = CostumePickerBox { selectCostumeName(it) }
    val layersBox = LayersBox(layers)
    val actorsBox = ActorsBox(this)
    val actorAttributesBox = ActorAttributesBox(this)

    val costumesPane = TitledPane("Costume Picker", costumeBox.build())
    val attributesPane = TitledPane("Attributes", actorAttributesBox.build())
    val stagesPane = TitledPane("Actors", actorsBox.build())
    val layersPane = TitledPane("Layers", layersBox.build())

    // NOTE. If this order changes, also change the index for SHOW_COSTUME_PICKER in MainWindow
    val sidePanes = listOf(costumesPane, attributesPane, stagesPane, layersPane)

    val costumeHistory = mutableListOf<String>()

    val editSnapsButton = EditorActions.SNAPS_EDIT.createButton { onEditSnaps() }
    val toggleGridButton = EditorActions.SNAP_TO_GRID_TOGGLE.createToggleButton { sceneResource.snapToGrid.enabled = !sceneResource.snapToGrid.enabled }
    val toggleGuidesButton = EditorActions.SNAP_TO_GUIDES_TOGGLE.createToggleButton { sceneResource.snapToGuides.enabled = !sceneResource.snapToGuides.enabled }
    val toggleSnapToOThersButton = EditorActions.SNAP_TO_OTHERS_TOGGLE.createToggleButton { sceneResource.snapToOthers.enabled = !sceneResource.snapToOthers.enabled }
    val toggleSnapRotationButton = EditorActions.SNAP_ROTATION_TOGGLE.createToggleButton { sceneResource.snapRotation.enabled = !sceneResource.snapRotation.enabled }

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
            add(EditorActions.UNDO) { onUndo() }
            add(EditorActions.REDO) { onRedo() }
            add(EditorActions.ZOOM_RESET) { layers.scale = 1.0 }
            add(EditorActions.ZOOM_IN1) { layers.scale *= 1.2 }
            add(EditorActions.ZOOM_IN2) { layers.scale *= 1.2 }
            add(EditorActions.ZOOM_OUT) { layers.scale /= 1.2 }
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


    fun findActorsAt(x: Double, y: Double, ignoreStageLock: Boolean = false): List<DesignActorResource> {
        val list = mutableListOf<DesignActorResource>()
        layers.visibleLayers().filter { ignoreStageLock || !it.isLocked }.forEach { stageLayer ->

            list.addAll(stageLayer.actorsAt(x, y).map { it as DesignActorResource })
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

        val resetAllZOrders = EditorActions.RESET_ZORDERS.createMenuItem { onResetZOrders() }

        menu.items.addAll(resetAllZOrders, SeparatorMenuItem())

        return menu
    }

    fun moveSelectTo(layer: StageLayer) {
        history.beginBatch()
        selection.toList().forEach { actorResource ->
            history.makeChange(MoveToLayer(actorResource, layer))
        }
        history.endBatch()
    }

    fun onEditSnaps() {
        SnapEditor(mapOf(
                "Grid" to sceneResource.snapToGrid,
                "Guides" to sceneResource.snapToGuides,
                "Others" to sceneResource.snapToOthers,
                "Rotation" to sceneResource.snapRotation
        )).show()
    }

    fun onEscape() {
        selection.clear()
        mouseHandler = Select()
    }

    fun onUndo() {
        if (history.canUndo()) {
            history.undo()
        }
    }

    fun onRedo() {
        if (history.canRedo()) {
            history.redo()
        }
    }

    fun addActor(actorResource: DesignActorResource, layer: StageLayer) {
        layer.stageConstraint.addActorResource(actorResource)
        layer.stageResource.actorResources.add(actorResource)
        actorResource.layer = layer

        sceneResource.fireChange(actorResource, ModificationType.NEW)
    }

    fun delete(actorResource: DesignActorResource) {
        actorResource.layer?.stageResource?.actorResources?.remove(actorResource)
        sceneResource.fireChange(actorResource, ModificationType.DELETE)
        selection.remove(actorResource)
    }

    fun onDelete() {
        val focus = scrollPane.scene.focusOwner
        if (focus is TextInputControl || focus is Spinner<*>) {
            ShortcutHelper.ignore()
        } else {
            history.makeChange(DeleteActors(selection))
        }
    }

    fun onResetZOrders() {
        history.beginBatch()
        sceneResource.stageResources.forEach { _, stageResource ->
            stageResource.actorResources.forEach { ar ->
                ar.costume()?.let { costume ->
                    if (ar.zOrder != costume.zOrder) {
                        history.makeChange(ChangeZOrder(ar, costume.zOrder))
                    }
                }
            }
        }
        history.endBatch()
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

    fun snapActor(actorResource: DesignActorResource, isNew: Boolean, useSnaps: Boolean) {

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

    open inner class MouseHandler {

        open fun onMousePressed(event: MouseEvent) {
            event.consume()
            // If we drag an actor, put all of the MoveActor Changes into one Batch.
            // onMouseRelease ends the batch.
            history.beginBatch()
        }

        open fun onDragDetected(event: MouseEvent) {
            event.consume()
        }

        open fun onMouseMoved(event: MouseEvent) {
            event.consume()
        }

        open fun onMouseDragged(event: MouseEvent) {
            event.consume()
        }

        open fun onMouseReleased(event: MouseEvent) {
            event.consume()
            history.endBatch()
        }

        open fun end() {}
    }

    inner class Select : MouseHandler() {

        var offsetX: Double = 0.0
        var offsetY: Double = 0.0

        override fun onMousePressed(event: MouseEvent) {
            super.onMousePressed(event)

            val wx = viewX(event)
            val wy = viewY(event)


            if (event.button == MouseButton.PRIMARY) {

                selection.forEach { ar ->
                    ar.draggedX = ar.x
                    ar.draggedY = ar.y
                }

                val handle = layers.glass.findDragHandle(wx, wy)
                if (handle != null) {
                    mouseHandler = AdjustDragHandle(handle)

                } else {
                    val ignoreLock = event.isAltDown // On my system Mouse + Alt moves a window, but Mouse + Alt + Shift works fine ;-)
                    val actors = findActorsAt(wx, wy, ignoreLock)
                    val highestActor = actors.firstOrNull()

                    if (highestActor == null) {
                        mouseHandler = SelectArea(wx, wy)
                    } else {

                        if (event.isControlDown) {
                            // Add or remove the top-most actor to/from the selection
                            if (selection.contains(highestActor)) {
                                selection.remove(highestActor)
                            } else {
                                selection.add(highestActor)
                            }

                        } else if (event.isShiftDown) {
                            if (actors.contains(selection.latest())) {
                                // Select the actor below the currently selected actor (i.e. with a higher index)
                                // This is useful when there are many actors on top of each other. We can get to any of them, by repeatedly shift-clicking
                                val i = actors.indexOf(selection.latest())
                                if (i == actors.size - 1) {
                                    selection.clearAndSelect(highestActor)
                                } else {
                                    selection.clearAndSelect(actors[i + 1])
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
                }

            } else if (event.button == MouseButton.MIDDLE || event.isAltDown) {
                mouseHandler = Pan()
            }
        }

        override fun onMouseMoved(event: MouseEvent) {
            layers.glass.hover(viewX(event), viewY(event))
        }

        // Move the selected actors
        override fun onMouseDragged(event: MouseEvent) {
            if (event.button == MouseButton.PRIMARY) {
                selection.selected().forEach { actorResource ->
                    actorResource.draggedX += dragDeltaX
                    actorResource.draggedY -= dragDeltaY
                    val oldX = actorResource.x
                    val oldY = actorResource.y
                    snapActor(actorResource, false, !event.isControlDown)
                    history.makeChange(MoveActor(actorResource, oldX, oldY))
                }
            }
        }

    }

    inner class SelectArea(val startX: Double, val startY: Double) : MouseHandler() {

        override fun onMouseDragged(event: MouseEvent) {
            val wx = viewX(event)
            val wy = viewY(event)

            val fromX = Math.min(wx, startX)
            val fromY = Math.min(wy, startY)
            val toX = Math.max(wx, startX)
            val toY = Math.max(wy, startY)

            selection.clear()
            layers.currentLayer?.stageResource?.actorResources?.forEach { actorResource ->
                actorResource as DesignActorResource

                if (actorResource.x >= fromX && actorResource.y >= fromY && actorResource.x <= toX && actorResource.y <= toY) {
                    selection.add(actorResource)
                }

            }
            layers.glass.draw {
                layers.glass.drawContent()
                with(layers.glass.canvas.graphicsContext2D) {
                    stroke = Color.RED
                    setLineDashes(3.0, 10.0)

                    strokeRect(fromX, fromY, toX - fromX, toY - fromY)
                }
            }
        }

        override fun onMouseReleased(event: MouseEvent) {
            super.onMouseReleased(event)
            layers.glass.draw()
            mouseHandler = Select()
        }
    }

    inner class Pan : MouseHandler() {
        override fun onMouseDragged(event: MouseEvent) {
            layers.panBy(dragDeltaX, -dragDeltaY)
        }

        override fun onMouseReleased(event: MouseEvent) {
            super.onMouseReleased(event)
            mouseHandler = Select()
        }
    }

    inner class AdjustDragHandle(val dragHandle: GlassLayer.DragHandle) : MouseHandler() {

        override fun onMouseDragged(event: MouseEvent) {
            dragHandle.moveTo(viewX(event), viewY(event), !event.isControlDown)
            selection.latest()?.let { sceneResource.fireChange(it, ModificationType.CHANGE) }
        }

        override fun onMouseReleased(event: MouseEvent) {
            super.onMouseReleased(event)
            mouseHandler = Select()
        }
    }

    inner class Stamp(val costumeName: String) : MouseHandler() {

        var newActor = DesignActorResource()

        init {
            newActor.costumeName = costumeName
            newActor.layer = layers.currentLayer()
            layers.glass.newActor = newActor
        }

        override fun end() {
            layers.glass.newActor = null
        }

        override fun onMouseMoved(event: MouseEvent) {

            newActor.draggedX = viewX(event)
            newActor.draggedY = viewY(event)
            snapActor(newActor, true, !event.isControlDown)
            layers.glass.dirty = true
        }

        override fun onMousePressed(event: MouseEvent) {

            layers.currentLayer?.let { layer ->
                val change = AddActor(newActor, layer)

                if (event.isShiftDown) {

                    newActor = DesignActorResource()
                    newActor.costumeName = costumeName
                    layers.glass.newActor = newActor

                } else {
                    layers.glass.newActor = null
                    selection.clearAndSelect(newActor)
                    mouseHandler = Select()
                }

                history.makeChange(change)
            }
        }
    }
}

