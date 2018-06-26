package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.scene.StageLayer
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

class MoveToLayer(
        private val actorResource : ActorResource,
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
