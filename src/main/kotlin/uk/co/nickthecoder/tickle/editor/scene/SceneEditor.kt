package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneListerner
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.editor.MainWindow


class SceneEditor(val sceneResource: SceneResource)

    : SceneListerner {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    var mouseHandler: MouseHandler? = Select()

    val selection = Selection()

    val layers = Layers(sceneResource, selection)

    var dirty = true

    val shortcuts = ShortcutHelper("SceneEditor", scrollPane)

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

        layers.stack.background = sceneResource.background.toJavaFX().background()

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

    fun findActorsAt(x: Float, y: Float): List<SceneActor> {
        val list = mutableListOf<SceneActor>()
        sceneResource.sceneStages.forEach { _, sceneStage ->
            sceneStage.sceneActors.forEach { sceneActor ->
                if (isSceneActorAt(sceneActor, x, y)) {
                    list.add(sceneActor)
                }
            }
        }
        return list
    }

    fun updateAttributesBox() {
        MainWindow.instance?.let { mainWindow ->
            val latest = selection.latest()
            if (latest == null) {
                mainWindow.propertiesPane.clear()
            } else {
                mainWindow.propertiesPane.show(ActorProperties(latest, sceneResource))
            }
        }
    }

    fun onEscape() {
        mouseHandler?.escape()
        selection.clear()
        mouseHandler = Select()
        updateAttributesBox()
        draw()
    }

    fun onDelete() {
        selection.selected().forEach { sceneActor ->
            sceneResource.sceneStages.values.forEach { sceneStage ->
                sceneStage.sceneActors.remove(sceneActor)
            }
        }
        selection.clear()
        updateAttributesBox()
        sceneResource.fireChange()
    }

    val costumeHistory = mutableListOf<String>()

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

        mouseHandler?.onMousePressed(event)
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
            val latest = selection.latest()

            if (event.button == MouseButton.PRIMARY) {

                if (latest != null && layers.glass.isNearRotationHandle(wx, wy)) {
                    mouseHandler = Rotate(latest)

                } else {
                    val actors = findActorsAt(wx, wy)
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
            layers.glass.highlightHandle(worldX(event), worldY(event))
        }

        override fun onMouseDragged(event: MouseEvent) {
            if (event.button == MouseButton.PRIMARY) {
                selection.selected().forEach { sceneActor ->
                    sceneActor.x += dragDeltaX.toFloat()
                    sceneActor.y -= dragDeltaY.toFloat()
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

    inner class Rotate(val sceneActor: SceneActor) : MouseHandler {
        override fun onMouseDragged(event: MouseEvent) {
            val dx = worldX(event) - sceneActor.x
            val dy = worldY(event) - sceneActor.y
            val atan = Math.atan2(dy.toDouble(), dx.toDouble())
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (event.isShiftDown) 15.0 else 1.0))
            val rotateBy = angle - sceneActor.directionRadians

            selection.forEach {
                it.directionRadians += rotateBy
            }
            sceneResource.fireChange()
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }

    inner class Stamp(val costumeName: String) : MouseHandler {

        var newActor = SceneActor()

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

