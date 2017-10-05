package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneResource


class SceneEditor(val sceneResource: SceneResource) {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    val gameWidth = Resources.instance.gameInfo.width
    val gameHeight = Resources.instance.gameInfo.height

    var mouseHandler: MouseHandler? = Select()

    val layers = Layers(sceneResource)

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


    fun findActorsOverlapping(x: Float, y: Float): List<SceneActor> {
        val list = mutableListOf<SceneActor>()
        sceneResource.sceneStages.forEach { _, sceneStage ->
            sceneStage.sceneActors.forEach { sceneActor ->
                if (sceneActor.isOverlapping(x, y)) {
                    list.add(sceneActor)
                }
            }
        }
        return list
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


    var mousePressedViewX: Double = 0.0
    var mousePressedViewY: Double = 0.0

    /**
     * The position in world coordinates where the mouse was pressed
     */
    var mousePressedX: Float = 0f
    var mousePressedY: Float = 0f

    /**
     * The current position in world coordinates (set by all mouse events)
     */
    var mouseX: Float = 0f
    var mouseY: Float = 0f

    fun onMousePressed(event: MouseEvent) {
        mouseX = layers.eventToWorldX(event)
        mouseY = layers.eventToWorldY(event)
        mousePressedX = mouseX
        mousePressedY = mouseY
        mousePressedViewX = event.x
        mousePressedViewY = event.y

        mouseHandler?.onMousePressed(event)
    }

    fun onDragDetected(event: MouseEvent) {
        mouseX = layers.eventToWorldX(event)
        mouseY = layers.eventToWorldY(event)

        mouseHandler?.onDragDetected(event)
    }

    fun onMouseMoved(event: MouseEvent) {
        mouseX = layers.eventToWorldX(event)
        mouseY = layers.eventToWorldY(event)

        mouseHandler?.onMouseMoved(event)
    }

    fun onMouseDragged(event: MouseEvent) {
        mouseX = layers.eventToWorldX(event)
        mouseY = layers.eventToWorldY(event)

        mouseHandler?.onMouseDragged(event)
    }

    fun onMouseReleased(event: MouseEvent) {
        mouseX = layers.eventToWorldX(event)
        mouseY = layers.eventToWorldY(event)

        mouseHandler?.onMouseReleased(event)
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

    inner class Select : MouseHandler {
        override fun onMousePressed(event: MouseEvent) {
            val actors = findActorsAt(mouseX, mouseY)
            println("Clicked on actors : $actors")
            if (event.button == MouseButton.MIDDLE || event.isAltDown) {
                println("Begin panning")
                mouseHandler = Pan()
            }
        }

    }

    inner class Pan : MouseHandler {
        override fun onMouseDragged(event: MouseEvent) {
            val dx = event.x - mousePressedViewX
            val dy = event.y - mousePressedViewY
            mousePressedViewX = event.x
            mousePressedViewY = event.y
            layers.panBy(dx, -dy)
        }

        override fun onMouseReleased(event: MouseEvent) {
            mouseHandler = Select()
        }
    }
}

