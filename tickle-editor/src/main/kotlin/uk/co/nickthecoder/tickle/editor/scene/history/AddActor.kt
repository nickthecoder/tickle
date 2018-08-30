package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.scene.StageLayer

class AddActor(
        private val actorResource: DesignActorResource,
        private val layer: StageLayer)
    : Change {

    override fun redo(sceneEditor: SceneEditor) {
        sceneEditor.addActor(actorResource, layer)
    }

    override fun undo(sceneEditor: SceneEditor) {
        sceneEditor.delete(actorResource)
    }

}
