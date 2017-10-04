package uk.co.nickthecoder.tickle.editor.tabs

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.editor.ImageCache


class SceneEditor(val sceneResource: SceneResource) {

    val scrollPane = ScrollPane()

    val borderPane = BorderPane()

    val gameWidth = Resources.instance.gameInfo.width.toDouble()
    val gameHeight = Resources.instance.gameInfo.height.toDouble()

    val canvas = Canvas(gameWidth, gameHeight)

    var gc = canvas.graphicsContext2D

    val mouseHandler: MouseHandler? = Select()

    fun build(): Node {

        with(scrollPane) {
            content = canvas
        }

        with(borderPane) {
            center = scrollPane
        }

        with(canvas) {
            addEventHandler(MouseEvent.MOUSE_PRESSED) { onMousePressed(it) }
            addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
            addEventHandler(MouseEvent.DRAG_DETECTED) { onDragDetected(it) }
            addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouseReleased(it) }
        }

        gc.transform(1.0, 0.0, 0.0, -1.0, 0.0, canvas.height)
        draw()
        return borderPane
    }

    fun draw() {

        drawBorder()

        gc.fill = Color.BLUE
        gc.fillRect(75.0, 75.0, 100.0, 10.00)

        sceneResource.sceneStages.forEach { _, sceneStage ->
            sceneStage.sceneActors.forEach { sceneActor ->
                pose(sceneActor)?.let { pose ->
                    gc.save()
                    gc.translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                    drawPose(pose)
                    gc.restore()
                }
            }
        }

    }

    fun drawBorder() {
        gc.stroke = Color.LIGHTCORAL
        gc.lineWidth = 1.0
        gc.setLineDashes(10.0, 3.0)
        gc.strokeLine(-1.0, -1.0, canvas.width + 1, -1.0)
        gc.strokeLine(canvas.width + 1, -1.0, canvas.width, canvas.height + 1)
        gc.strokeLine(canvas.width + 1, canvas.height + 1, -1.0, canvas.height + 1)
        gc.strokeLine(-1.0, canvas.height + 1, -1.0, -1.0)
        gc.setLineDashes()
    }

    fun drawPose(pose: Pose) {
        val image = image(pose)
        // TODO Rotation.
        gc.drawImage(
                image,
                pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                -pose.offsetX.toDouble(), -pose.offsetY.toDouble(), pose.rect.width.toDouble(), pose.rect.height.toDouble())
    }

    fun image(pose: Pose): Image? {
        pose.texture.file?.let {
            return ImageCache.image(it)
        }
        return null
    }

    private val poseMap = mutableMapOf<SceneActor, Pose?>()

    fun pose(sceneActor: SceneActor): Pose? {
        if (poseMap.containsKey(sceneActor)) {
            return poseMap[sceneActor]
        } else {
            val pose = Resources.instance.optionalCostume(sceneActor.costumeName)?.events?.get("default")?.choosePose()
            poseMap[sceneActor] = pose
            return pose
        }
    }

    fun isPoseOverlapping(pose: Pose?, x: Float, y: Float): Boolean {
        pose ?: return false
        return x > -pose.offsetX && x < pose.rect.width - pose.offsetX &&
                y > -pose.offsetY && y < pose.rect.height - pose.offsetY
    }

    fun isSceneActorOverlapping(sceneActor: SceneActor, x: Float, y: Float): Boolean {
        val tx = x - sceneActor.x
        val ty = y - sceneActor.y
        return isPoseOverlapping(pose(sceneActor), tx, ty)
    }

    /**
     * x,y are relative to the "offset point" of the pose, with the y axis pointing upwards
     */
    fun isPixelIsOpaque(pose: Pose?, x: Float, y: Float, threshold: Double = 0.05): Boolean {
        pose ?: return false
        val px = pose.rect.left + x + pose.offsetX
        val py = pose.rect.top + pose.rect.height - (y + pose.offsetY)
        pose.texture.file?.let { file ->
            return ImageCache.image(file).pixelReader.getColor(px.toInt(), py.toInt()).opacity > threshold
        }
        return false
    }

    fun isSceneActorAt(sceneActor: SceneActor, x: Float, y: Float): Boolean {
        val tx = x - sceneActor.x
        val ty = y - sceneActor.y
        val pose = pose(sceneActor)
        if (isPoseOverlapping(pose, tx, ty)) {
            // println("Pose rect = ${pose?.rect}")
            return isPixelIsOpaque(pose, tx, ty)
        } else {
            return false
        }
    }

    fun findActorsOverlapping(x: Float, y: Float): List<SceneActor> {
        val list = mutableListOf<SceneActor>()
        sceneResource.sceneStages.forEach { _, sceneStage ->
            sceneStage.sceneActors.forEach { sceneActor ->
                if (isSceneActorOverlapping(sceneActor, x, y)) {
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


    fun eventToWorldX(event: MouseEvent): Float {
        // TODO Update this when panning is implemented
        return event.x.toFloat()
    }

    fun eventToWorldY(event: MouseEvent): Float {
        // TODO Update this when panning is implemented
        return (canvas.height - event.y).toFloat()
    }


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
        mouseX = eventToWorldX(event)
        mouseY = eventToWorldY(event)
        mousePressedX = mouseX
        mousePressedY = mouseY

        mouseHandler?.onMousePressed(event)
    }

    fun onDragDetected(event: MouseEvent) {
        mouseX = eventToWorldX(event)
        mouseY = eventToWorldY(event)
        mouseHandler?.onDragDetected(event)
    }

    fun onMouseMoved(event: MouseEvent) {
        mouseX = eventToWorldX(event)
        mouseY = eventToWorldY(event)
        mouseHandler?.onMouseMoved(event)
    }

    fun onMouseReleased(event: MouseEvent) {
        mouseX = eventToWorldX(event)
        mouseY = eventToWorldY(event)
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

        fun onMouseReleased(event: MouseEvent) {
            event.consume()
        }
    }

    inner class Select : MouseHandler {
        override fun onMousePressed(event: MouseEvent) {
            val actors = findActorsOverlapping(mouseX, mouseY)
            val pixelActors = findActorsAt(mouseX, mouseY)
            println("Overlapping = $actors, Pixels = $pixelActors")
        }

    }
}

