package uk.co.nickthecoder.tickle.editor.tabs

import javafx.beans.Observable
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.gui.MySplitPane
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.util.ImageCache
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.resources.ResourcesListener
import uk.co.nickthecoder.tickle.util.YDownRect

class PosesEditor(val texture: Texture)

    : ResourcesListener {

    val splitPane = MySplitPane()

    val posesList = ListView<String>()

    val imageView = ImageView(ImageCache.image(texture.file!!))

    val container = Group()

    val rectangles = mutableMapOf<String, Rectangle>()

    val scrollPane = ScrollPane()

    val backgroundColor = Color(0.2, 0.2, 0.2, 1.0)

    val unselectedColor = Color(0.8, 0.8, 0.8, 0.5)

    var colorIndex = 0

    val colors = listOf(Color.LIGHTGRAY, Color.DARKGRAY, Color.BLACK, Color.WHITE)


    var dragging: Boolean = false
    var dragStartX = 0
    var dragStartY = 0

    init {
        Resources.instance.listeners.add(this)
    }

    fun closed() {
        Resources.instance.listeners.remove(this)
    }

    fun build(): Node {

        scrollPane.content = container

        with(splitPane) {
            right = posesList
            left = scrollPane
            dividerRatio = 0.7
        }

        val background = Rectangle(texture.width.toDouble(), texture.height.toDouble())
        background.fill = backgroundColor
        container.children.addAll(background, imageView)

        posesList.selectionModel.selectedItems.addListener { _: Observable ->
            selectPose(posesList.selectionModel.selectedItem)
        }
        container.onMouseClicked = EventHandler { onMouseClicked(it) }
        container.onMousePressed = EventHandler { dragging = false }
        container.onDragDetected = EventHandler { onDragDetected(it) }
        container.onMouseReleased = EventHandler { onMouseReleased(it) }

        Resources.instance.poses.items().forEach { name, pose ->
            if (pose.texture === texture) {
                includePose(name, pose)
            }
        }

        return splitPane
    }

    override fun resourceAdded(resource: Any, name: String) {
        if (resource is Pose && resource.texture === texture) {
            includePose(name, resource)
        }
    }

    override fun resourceRemoved(resource: Any, name: String) {
        if (resource is Pose && resource.texture === texture) {
            removePose(name)
        }
    }

    override fun resourceChanged(resource: Any) {
        if (resource is Pose) {
            Resources.instance.poses.findName(resource)?.let { name ->
                removePose(name)
                includePose(name, resource)
            }
        }
    }

    fun includePose(name: String, pose: Pose) {
        posesList.items.add(name)

        val rectangle = Rectangle()
        rectangles[name] = rectangle
        with(rectangle) {
            fill = unselectedColor
            x = pose.rect.left.toDouble()
            y = pose.rect.top.toDouble()
            width = pose.rect.width.toDouble()
            height = pose.rect.height.toDouble()
        }
        container.children.add(1, rectangle)
    }

    fun removePose(name: String) {
        posesList.items.remove(name)
        rectangles[name]?.let {
            rectangles.remove(name)
            container.children.remove(it)
        }
    }

    var selectedPoseName: String? = null

    fun selectPose(name: String?) {
        selectedPoseName?.let {
            rectangles[it]?.fill = unselectedColor
        }
        selectedPoseName = name

        selectedPoseName?.let {
            rectangles[it]?.fill = colors[colorIndex]
        }
    }

    fun onMouseClicked(event: MouseEvent) {
        posesList.items.forEachIndexed { i, name ->
            val pose = Resources.instance.poses.find(name)!!
            if (pose.rect.contains(event.x.toInt(), event.y.toInt())) {
                posesList.selectionModel.clearAndSelect(i)
                if (event.clickCount == 2) {
                    MainWindow.instance.openTab(name, pose)
                }
                event.consume()
                return
            }
        }
        if (event.clickCount == 2) {
            autoCreatePose(event)
            event.consume()
        }
    }

    fun onDragDetected(event: MouseEvent) {
        dragStartX = event.x.toInt()
        dragStartY = event.y.toInt()
        dragging = true
    }

    fun onMouseReleased(event: MouseEvent) {
        if (dragging) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            val rect = YDownRect(Math.min(dragStartX, x), Math.min(dragStartY, y), Math.max(dragStartX, x), Math.max(dragStartY, y))

            TaskPrompter(NewPoseTask(texture, rect)).placeOnStage(Stage())
            dragging = false
        }
    }

    fun autoCreatePose(event: MouseEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val rect = PixelScanner(imageView.image, x, y).scan()
        val offsetX = x - rect.left
        val offsetY = rect.bottom - y
        TaskPrompter(NewPoseTask(texture, rect, offsetX, offsetY)).placeOnStage(Stage())
    }
}


class NewPoseTask(
        val texture: Texture,
        val rect: YDownRect,
        val offsetX: Int? = null,
        val offsetY: Int? = null)

    : AbstractTask() {

    val nameP = StringParameter("poseName")

    override val taskD = TaskDescription("addNewPose")
            .addParameters(nameP)

    override val taskRunner = UnthreadedTaskRunner(this)

    override fun run() {
        val pose = Pose(texture, rect)
        pose.offsetX = (offsetX ?: rect.width / 2).toDouble()
        pose.offsetY = (offsetY ?: rect.height / 2).toDouble()

        Resources.instance.poses.add(nameP.value, pose)
    }
}

private class PixelScanner(val image: Image, x: Int, y: Int, val threshold: Double = 0.05, val maxIterations: Int = 10000) {

    val rect = YDownRect(Math.max(0, x - 2), Math.max(0, y - 2), Math.min(image.width.toInt() - 1, x + 2), Math.min(image.height.toInt(), y + 2))

    val maxX = image.width.toInt() - 1
    val maxY = image.height.toInt() - 1

    val reader = image.pixelReader

    fun scan(): YDownRect {

        for (i in 0..maxIterations) {
            val result = scanLeft() && scanUp() && scanRight() && scanDown()
            if (result) {
                return rect
            }
        }
        println("Failed after reaching the limit of $maxIterations iterations")
        return rect
    }

    fun isTranspanret(x: Int, y: Int): Boolean {
        return reader.getColor(x, y).opacity <= threshold
    }

    fun scanLeft(): Boolean {
        if (rect.left <= 0) return true

        for (y in rect.top..rect.bottom) {
            if (!isTranspanret(rect.left, y)) {
                rect.left--
                return false
            }
        }
        return true
    }

    fun scanRight(): Boolean {
        if (rect.right >= maxX) return true

        for (y in rect.top..rect.bottom) {
            if (!isTranspanret(rect.right, y)) {
                rect.right++
                return false
            }
        }
        return true
    }

    fun scanUp(): Boolean {
        if (rect.top <= 0) return true

        for (x in rect.left..rect.right) {
            if (!isTranspanret(x, rect.top)) {
                rect.top--
                return false
            }
        }
        return true
    }

    fun scanDown(): Boolean {
        if (rect.bottom >= maxY) return true

        for (x in rect.left..rect.right) {
            if (!isTranspanret(x, rect.bottom)) {
                rect.bottom++
                return false
            }
        }
        return true
    }
}
