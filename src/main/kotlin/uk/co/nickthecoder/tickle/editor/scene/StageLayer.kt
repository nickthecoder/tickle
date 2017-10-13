package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import uk.co.nickthecoder.tickle.*

class StageLayer(
        val sceneResource: SceneResource,
        val stageName: String,
        val stageResource: StageResource)

    : Layer(), SceneResourceListener {

    private var dirty = true
        set(v) {
            if (field != v) {
                field = v
                Platform.runLater {
                    if (dirty) {
                        draw()
                    }
                }
            }
        }

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

    init {
        sceneResource.listeners.add(this)
    }

    override fun drawContent() {

        stageResource.actorResources.forEach { actorResource ->
            drawActor(actorResource)
        }
        dirty = false
    }

    override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
        if (isVisible) {
            dirty = true
            Platform.runLater {
                if (dirty) {
                    draw()
                }
            }
        }
    }

}
