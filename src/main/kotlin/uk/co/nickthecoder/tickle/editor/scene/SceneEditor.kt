package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneResource


class SceneEditor(val sceneResource: SceneResource) {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    var mouseHandler: MouseHandler? = Select()

    val selection = Selection()

    val layers = Layers(sceneResource, selection)


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

        draw()
        return borderPane
    }

    fun draw() {
        layers.draw()
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
    var dragDeltaY: Double = 0.0

    fun onMouseDragged(event: MouseEvent) {
        dragDeltaX = event.x - dragPreviousX
        dragDeltaY = -(event.y - dragPreviousY)

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

            } else if (event.button == MouseButton.MIDDLE || event.isAltDown) {
                mouseHandler = Pan()
            }
        }

        override fun onMouseDragged(event: MouseEvent) {
            if (event.button == MouseButton.PRIMARY) {
                selection.selected().forEach { sceneActor ->
                    sceneActor.x += dragDeltaX.toFloat()
                    sceneActor.y += dragDeltaY.toFloat()
                }
                if (selection.isNotEmpty()) {
                    layers.draw()
                }
            }
        }

    }

    inner class Pan : MouseHandler {
        override fun onMouseDragged(event: MouseEvent) {
            layers.panBy(dragDeltaX, dragDeltaY)
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }

    inner class Rotate(val sceneActor: SceneActor) : MouseHandler {
        override fun onMouseDragged(event: MouseEvent) {
            val dx = worldX(event) - sceneActor.x
            val dy = worldY(event) - sceneActor.y
            sceneActor.directionRadians = Math.atan2(dy.toDouble(), dx.toDouble())
            draw()
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }
}

