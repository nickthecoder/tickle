package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.tickle.SceneResource
import uk.co.nickthecoder.tickle.StageResource

class StageLayer(
        val sceneResource: SceneResource,
        val stageName: String,
        val stageResource: StageResource)

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

        stageResource.actorResources.forEach { actorResource ->
            drawActor( actorResource )
        }
    }

}
