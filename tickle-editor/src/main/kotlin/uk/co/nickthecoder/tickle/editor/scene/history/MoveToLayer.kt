package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.scene.StageLayer

class MoveToLayer(
        private val actorResource: DesignActorResource,
        private val newLayer: StageLayer)
    : Change {

    private val oldLayer = actorResource.layer

    override fun redo(sceneEditor: SceneEditor) {
        sceneEditor.delete(actorResource)
        newLayer.stageResource.actorResources.add(actorResource)
        actorResource.layer = newLayer

        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.NEW)
    }

    override fun undo(sceneEditor: SceneEditor) {
        sceneEditor.delete(actorResource)
        if (oldLayer != null) {
            oldLayer.stageResource.actorResources.add(actorResource)
            actorResource.layer = oldLayer
        }
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.NEW)
    }
}
