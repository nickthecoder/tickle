package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.DesignSceneResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.resources.SceneResourceListener
import uk.co.nickthecoder.tickle.editor.util.isAt
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.StageConstraint
import uk.co.nickthecoder.tickle.resources.StageResource
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.util.sortedBackwardsWith

class StageLayer(

        val sceneResource: DesignSceneResource,
        val stageName: String,
        val stageResource: StageResource,
        val stageView: StageView,
        val stageConstraint: StageConstraint)

    : Layer(), SceneResourceListener {

    protected var dirty = true
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
        stageResource.actorResources.forEach { actor ->
            actor.draggedX = actor.x
            actor.draggedY = actor.y
            (actor as DesignActorResource).layer = this
            stageConstraint.addActorResource(actor)
        }
        sceneResource.listeners.add(this)
    }

    fun actorsAt(x: Double, y: Double): Iterable<ActorResource> {
        return stageResource.actorResources.filter { it.isAt(x, y) }.sortedBackwardsWith(stageView.comparator)
    }

    override fun actorModified(sceneResource: DesignSceneResource, actorResource: DesignActorResource, type: ModificationType) {
        if (isVisible) {
            dirty = true
            Platform.runLater {
                if (dirty) {
                    draw()
                }
            }
        }
    }

    override fun drawContent() {

        stageResource.actorResources.sortedWith(stageView.comparator).forEach { actorResource ->
            drawActor(actorResource)
        }
        dirty = false
    }

}
