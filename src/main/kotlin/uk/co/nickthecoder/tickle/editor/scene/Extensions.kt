package uk.co.nickthecoder.tickle.editor.scene

import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.editor.util.ImageCache

/*
Contains may extension functions, helpful for the SceneEditor, that I don't want on the actual objects.
 */


fun Pose.image(): Image? {
    //texture.file?.let {
    //    return ImageCache.image(it)
    //}
    return ImageCache.image(texture)
}

fun Pose.isOverlapping(x: Double, y: Double): Boolean {
    return x > -offsetX && x < rect.width - offsetX &&
            y > -offsetY && y < rect.height - offsetY
}

fun SceneActor.isOverlapping(x: Double, y: Double): Boolean {
    val tx = x - this.x
    val ty = y - this.y
    return pose?.isOverlapping(tx, ty) ?: false
}

/**
 * x,y are relative to the "offset point" of the pose, with the y axis pointing upwards
 */
fun isPixelIsOpaque(pose: Pose?, x: Double, y: Double, threshold: Double = 0.05): Boolean {
    pose ?: return false
    val px = pose.rect.left + x + pose.offsetX
    val py = pose.rect.top + pose.rect.height - (y + pose.offsetY)
    pose.texture.file?.let { file ->
        return ImageCache.image(file).pixelReader.getColor(px.toInt(), py.toInt()).opacity > threshold
    }
    return false
}

fun isSceneActorAt(sceneActor: SceneActor, x: Double, y: Double): Boolean {
    val tx = x - sceneActor.x
    val ty = y - sceneActor.y
    val pose = sceneActor.pose
    if (pose?.isOverlapping(tx, ty) ?: false) {
        // println("Pose rect = ${pose?.rect}")
        return isPixelIsOpaque(pose, tx, ty)
    } else {
        return false
    }
}

fun Costume.pose() = events["default"]?.choosePose()
fun Costume.fontResource() = events["default"]?.chooseFontResource()

fun Pose.imageView(): ImageView? {
    texture.file?.let { file ->
        val iv = ImageView(ImageCache.image(file))
        iv.viewport = Rectangle2D(rect.left.toDouble(), rect.top.toDouble(), rect.width.toDouble(), rect.height.toDouble())
        return iv
    }
    return null
}

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

fun SceneActor.costume(): Costume? = Resources.instance.optionalCostume(costumeName)

fun uk.co.nickthecoder.tickle.graphics.Color.toJavaFX(): javafx.scene.paint.Color {
    return javafx.scene.paint.Color(red.toDouble(), green.toDouble(), blue.toDouble(), alpha.toDouble())
}

fun javafx.scene.paint.Color.toTickle(): uk.co.nickthecoder.tickle.graphics.Color {
    return uk.co.nickthecoder.tickle.graphics.Color(red.toFloat(), green.toFloat(), blue.toFloat(), opacity.toFloat())
}

fun javafx.scene.paint.Color.background() = Background(BackgroundFill(this, CornerRadii(0.0), Insets(0.0)))
