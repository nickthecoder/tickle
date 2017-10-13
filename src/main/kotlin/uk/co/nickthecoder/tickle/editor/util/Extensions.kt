package uk.co.nickthecoder.tickle.editor.util

import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import uk.co.nickthecoder.tickle.ActorResource
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.graphics.Color

/*
 Contains may extension functions, used from within the SceneEditor.
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

// COSTUME

fun Costume.pose() = events["default"]?.choosePose()

fun Costume.textStyle() = events["default"]?.chooseTextStyle()

fun Costume.thumbnail(size: Double): Node? {
    val pose = pose()
    if (pose != null) {
        val iv = pose.imageView()
        if (iv != null) {
            iv.isPreserveRatio = true
            if (iv.viewport.width > iv.viewport.height) {
                iv.fitWidth = size
            } else {
                iv.fitHeight = size
            }
        }
        return iv
    }
    return Label("?")
}

// SCENE ACTOR

fun ActorResource.isOverlapping(x: Double, y: Double): Boolean {
    val tx = x - this.x
    val ty = y - this.y
    return pose?.isOverlapping(tx, ty) ?: false
}

fun ActorResource.costume(): Costume? = Resources.instance.optionalCostume(costumeName)

fun ActorResource.isAt(x: Double, y: Double): Boolean {
    var tx = x - this.x
    var ty = y - this.y
    // TODO Need to account for rotation and scale!?!?
    pose?.let { pose ->
        return pose.isOverlapping(tx, ty) && pose.isPixelIsOpaque(tx, ty)
    }
    textStyle?.let { textStyle ->
        val offestX = textStyle.offsetX(displayText)
        val offsetY = textStyle.offsetY(displayText)
        val width = textStyle.width(displayText)
        val height = textStyle.height(displayText)
        tx += offestX
        ty += height - offsetY
        return tx > 0 && ty > 0 && tx < width && ty < height

    }
    return false
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
