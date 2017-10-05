package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.image.Image
import uk.co.nickthecoder.tickle.Pose
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