package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import uk.co.nickthecoder.tickle.editor.util.isAt
import uk.co.nickthecoder.tickle.resources.*
import uk.co.nickthecoder.tickle.stage.StageView

class StageLayer(
        val sceneResource: SceneResource,
        val stageName: String,
        val stageResource: StageResource,
        val stageView: StageView,
        val stageConstraint: StageConstraint)

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
        stageResource.actorResources.forEach { actor ->
            actor.draggedX = actor.x
            actor.draggedY = actor.y
            actor.layer = this
            stageConstraint.addActorResource(actor)
        }
    }

    fun actorsAt(x: Double, y: Double): Iterable<ActorResource> {
        return stageView.orderActors(stageResource.actorResources, true).filter { it.isAt(x, y) }
    }

    override fun drawContent() {

        stageView.orderActors(stageResource.actorResources, false).forEach { actorResource ->
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
