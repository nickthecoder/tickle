package uk.co.nickthecoder.tickle.editor.scene

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

        sceneStage.sceneActors.forEach { sceneActor ->
            drawActor( sceneActor )
        }
    }

}
