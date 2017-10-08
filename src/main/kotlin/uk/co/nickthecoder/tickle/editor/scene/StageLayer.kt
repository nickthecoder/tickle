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

    var isLocked: Boolean = false
        set(v) {
            field = v
            canvas.opacity = if (v) 0.5 else 1.0
        }

    override fun drawContent() {

        sceneStage.sceneActors.forEach { sceneActor ->
            drawActor( sceneActor )
        }
    }

}
