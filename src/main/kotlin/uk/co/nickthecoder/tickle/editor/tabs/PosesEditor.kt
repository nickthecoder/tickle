package uk.co.nickthecoder.tickle.editor.tabs

import javafx.beans.Observable
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.editor.ImageCache
import uk.co.nickthecoder.tickle.graphics.Texture

class PosesEditor(val texture: Texture) {

    val borderPane = BorderPane()

    val posesList = ListView<String>()

    val imageView = ImageView(ImageCache.image(texture.file!!))

    val container = Group()

    val rectangles = mutableMapOf<String, Rectangle>()

    val scrollPane = ScrollPane()

    val unselectedColor = Color(0.8, 0.8, 0.8, 0.5)

    var colorIndex = 0

    val colors = listOf(Color.LIGHTGRAY, Color.DARKGRAY, Color.BLACK, Color.WHITE)

    fun build(): Node {

        Resources.instance.poses().forEach { name, pose ->
            includePose(name, pose)
        }

        scrollPane.content = container

        borderPane.left = posesList
        borderPane.center = scrollPane

        container.children.add(imageView)

        posesList.selectionModel.selectedItems.addListener { _: Observable ->
            selectPose(posesList.selectionModel.selectedItem)
        }
        container.onMouseClicked = EventHandler { onMouseClicked(it) }

        return borderPane
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
        container.children.add(rectangle)
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
            val pose = Resources.instance.pose(name)
            if (pose.rect.contains(event.x.toInt(), event.y.toInt())) {
                posesList.selectionModel.clearAndSelect(i)
                return
            }
        }
    }

}

