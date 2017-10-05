package uk.co.nickthecoder.tickle.editor.scene

import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import uk.co.nickthecoder.tickle.Costume
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.editor.ImageCache

/*
Contains may extension functions, helpful for the SceneEditor, that I don't want on the actual objects.
 */


fun Pose.image(): Image? {
    texture.file?.let {
        return ImageCache.image(it)
    }
    return null
}

fun Pose.isOverlapping(x: Float, y: Float): Boolean {
    return x > -offsetX && x < rect.width - offsetX &&
            y > -offsetY && y < rect.height - offsetY
}

fun SceneActor.isOverlapping(x: Float, y: Float): Boolean {
    val tx = x - this.x
    val ty = y - this.y
    return pose?.isOverlapping(tx, ty) ?: false
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
    val pose = sceneActor.pose
    if (pose?.isOverlapping(tx, ty) ?: false) {
        // println("Pose rect = ${pose?.rect}")
        return isPixelIsOpaque(pose, tx, ty)
    } else {
        return false
    }
}

fun Costume.pose() = events["default"]?.choosePose()

fun Costume.imageView(): ImageView? {
    pose()?.let { pose ->
        pose.texture.file?.let { file ->
            val iv = ImageView(ImageCache.image(file))
            iv.viewport = Rectangle2D(pose.rect.left.toDouble(), pose.rect.top.toDouble(), pose.rect.width.toDouble(), pose.rect.height.toDouble())
            return iv
        }
    }
    return null
}

fun Costume.thumbnail(size: Double): ImageView? {
    val iv = imageView()
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

fun SceneActor.costume(): Costume? = Resources.instance.optionalCostume(costumeName)

fun uk.co.nickthecoder.tickle.graphics.Color.toJavaFX(): javafx.scene.paint.Color {
    return javafx.scene.paint.Color(red.toDouble(), green.toDouble(), blue.toDouble(), alpha.toDouble())
}

fun javafx.scene.paint.Color.toTickle(): uk.co.nickthecoder.tickle.graphics.Color {
    return uk.co.nickthecoder.tickle.graphics.Color(red.toFloat(), green.toFloat(), blue.toFloat(), opacity.toFloat())
}

fun javafx.scene.paint.Color.background() = Background(BackgroundFill(this, CornerRadii(0.0), Insets(0.0)))
