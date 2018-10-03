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
package uk.co.nickthecoder.tickle.editor.util

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.EditorAction
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.ResourceType
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Deletable

/*
 Contains many extension functions, used from within the SceneEditor.
 These are functions that I don't want on the actual objects, because I don't want them available
 from a running Game, and some may have dependencies only available from the editor module, which will not
 be in the class path during an actual game.
 e.g. I may not want a dependency on awt, or javafx during an actual game, which will make porting a game to
 Android easier.
*/

// POSE

fun Pose.image(): Image? {
    return ImageCache.image(texture)
}

fun Pose.imageView(): ImageView? {
    texture.file?.let { file ->
        val iv = ImageView(ImageCache.image(file))
        iv.viewport = Rectangle2D(rect.left.toDouble(), rect.top.toDouble(), rect.width.toDouble(), rect.height.toDouble())
        return iv
    }
    return null
}

fun Pose.thumbnail(size: Int): ImageView? {
    val iv = imageView()

    if (iv != null) {
        if (iv.viewport.width > size || iv.viewport.height > size) {
            iv.isPreserveRatio = true
            if (iv.viewport.width > iv.viewport.height) {
                iv.fitWidth = size.toDouble()
            } else {
                iv.fitHeight = size.toDouble()
            }
        }
    }
    return iv
}

fun Pose.isOverlapping(x: Double, y: Double): Boolean {
    return x > -offsetX && x < rect.width - offsetX &&
            y > -offsetY && y < rect.height - offsetY
}

/**
 * x,y are relative to the "offset point" of the pose, with the y axis pointing upwards
 */
fun Pose.isPixelIsOpaque(x: Double, y: Double, threshold: Double = 0.05): Boolean {
    val px = rect.left + x + offsetX
    val py = rect.top + rect.height - (y + offsetY)
    texture.file?.let { file ->
        return ImageCache.image(file).pixelReader.getColor(px.toInt(), py.toInt()).opacity > threshold
    }
    return false
}


// ACTOR RESOURCE

fun ActorResource.isOverlapping(x: Double, y: Double): Boolean {
    val tx = x - this.x
    val ty = y - this.y
    return editorPose?.isOverlapping(tx, ty) ?: false
}

fun ActorResource.isAt(x: Double, y: Double): Boolean {
    var tx = x - this.x
    var ty = y - this.y
    val poseDirection = pose?.direction?.radians ?: ninePatch?.pose?.direction?.radians ?: 0.0
    if (poseDirection != direction.radians) {
        val sin = Math.sin(poseDirection - direction.radians)
        val cos = Math.cos(poseDirection - direction.radians)
        val ttx = cos * tx - sin * ty
        ty = cos * ty + sin * tx
        tx = ttx
    }

    tx /= scale.x
    ty /= scale.y

    if (isSizable()) {

        tx += sizeAlignment.x * size.x
        ty += sizeAlignment.y * size.y
        return tx >= 0 && ty >= 0 && tx < size.x && ty < size.y

    } else {

        editorPose?.let { pose ->
            return pose.isOverlapping(tx, ty) && pose.isPixelIsOpaque(tx, ty)
        }
        textStyle?.let { textStyle ->
            val offsetX = textStyle.offsetX(displayText)
            val offsetY = textStyle.offsetY(displayText)
            val width = textStyle.width(displayText)
            val height = textStyle.height(displayText)
            tx += offsetX
            ty += height - offsetY
            return tx > 0 && ty > 0 && tx < width && ty < height

        }
    }

    return false
}

fun ActorResource.offsetX(): Double {
    editorPose?.let { pose ->
        return pose.offsetX
    }
    textStyle?.let { textStyle ->
        return textStyle.offsetX(text)
    }
    return 0.0
}

fun ActorResource.offsetY(): Double {
    editorPose?.let { pose ->
        return pose.offsetY
    }
    textStyle?.let { textStyle ->
        return textStyle.height(text) - textStyle.offsetY(text)
    }
    return 0.0
}

// COLOR Conversions

fun uk.co.nickthecoder.tickle.graphics.Color.toJavaFX(): javafx.scene.paint.Color {
    return javafx.scene.paint.Color(red.toDouble(), green.toDouble(), blue.toDouble(), alpha.toDouble())
}

fun javafx.scene.paint.Color.toTickle(): uk.co.nickthecoder.tickle.graphics.Color {
    return uk.co.nickthecoder.tickle.graphics.Color(red.toFloat(), green.toFloat(), blue.toFloat(), opacity.toFloat())
}

private val squareCorners = CornerRadii(0.0)

private val noInsets = Insets(0.0)

fun Color.background() = Background(BackgroundFill(toJavaFX(), squareCorners, noInsets))

fun Alert.addStyleSheet() {
    dialogPane.stylesheets.add(MainWindow::class.java.getResource("tickle.css").toExternalForm())
}

fun Deletable.deletePrompted(name: String) {

    val usedBy = dependables()

    val breakables = FlowPane()
    val unbreakables = FlowPane()
    val resourceLabel = ResourceType.resourceType(this)?.label ?: ""

    for (dependency in usedBy) {
        val editButton = createEditButton(dependency)
        if (dependency.isBreakable(this)) {
            breakables.children.add(editButton)
        } else {
            unbreakables.children.add(editButton)
        }
    }

    if (unbreakables.children.isNotEmpty()) {

        val vBox = VBox().apply { styleClass.add("form") }
        val heading = Label("Because of the following dependencies : ").apply { styleClass.add("heading") }
        vBox.children.addAll(heading, unbreakables)

        if (breakables.children.isNotEmpty()) {
            vBox.children.addAll(Label("These dependencies also exist, but can be broken : "), breakables)
        }

        val alert = Alert(Alert.AlertType.INFORMATION)

        with(alert) {
            addStyleSheet()
            title = "Cannot Delete $resourceLabel '$name'"
            headerText = title
            dialogPane.content = vBox
            showAndWait()
        }

    } else {

        val alert = Alert(Alert.AlertType.CONFIRMATION)

        with(alert) {
            addStyleSheet()
            title = "Delete $resourceLabel '$name'"
            headerText = title

            if (breakables.children.isNotEmpty()) {

                val vBox = VBox().apply { styleClass.add("form") }
                val label = Label("This will break the following dependencies : ")
                vBox.children.addAll(label, breakables)
                dialogPane.content = vBox
            }

            showAndWait()

        }

        if (alert.result == ButtonType.OK) {
            usedBy.forEach { it.breakDependency(this) }
            delete()
        }
    }
}

fun createEditButton(resource: Any): Button {
    val name = Resources.instance.findName(resource) ?: "<unknown>"

    val button = Button(name)
    button.onAction = EventHandler {
        MainWindow.instance.openTab(name, resource)
    }

    val resourceType = ResourceType.resourceType(resource)
    resourceType?.let {
        button.text += " (${resourceType.label})"
        button.graphic = ImageView(EditorAction.imageResource(resourceType.graphicName))
    }
    return button
}
