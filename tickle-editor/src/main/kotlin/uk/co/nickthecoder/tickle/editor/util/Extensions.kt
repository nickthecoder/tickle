package uk.co.nickthecoder.tickle.editor.util

import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.ActorResource

/*
 Contains many extension functions, used from within the SceneEditor.
 These are functions that I don't want on the actual objects, because I don't want them available
 from a running Game, and some may have dependancies only available from the editor module, which will not
 be in the class path during an actual game.
 e.g. I may not want a dependancy on awt, or javafx during an actual game, which will make porting a game to
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
    val poseDirection = pose?.direction?.radians ?: 0.0
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

