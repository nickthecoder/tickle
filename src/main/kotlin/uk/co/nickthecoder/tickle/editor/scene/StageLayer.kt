package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.canvas.GraphicsContext
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.SceneStage

class StageLayer(
        val sceneResource: SceneResource,
        val stageName: String,
        val sceneStage: SceneStage)

    : Layer() {


    var isVisible: Boolean = true
        set(v) {
            field = v
            canvas.isVisible = v
        }

    var isEditable: Boolean = true
        set(v) {
            field = v
            // Non-editable layers are semi-transparent.
            canvas.opacity = if (v) 1.0 else 0.5
        }

    override fun drawContent() {

        val gc = canvas.graphicsContext2D

        sceneStage.sceneActors.forEach { sceneActor ->
            sceneActor.pose?.let { pose ->
                gc.save()
                gc.translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                drawPose(gc, pose)
                gc.restore()
            }
        }
    }

    fun drawPose(gc: GraphicsContext, pose: Pose) {
        val image = pose.image()
        // TODO Rotation.
        gc.drawImage(
                image,
                pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                -pose.offsetX.toDouble(), -pose.offsetY.toDouble(), pose.rect.width.toDouble(), pose.rect.height.toDouble())
    }

}
