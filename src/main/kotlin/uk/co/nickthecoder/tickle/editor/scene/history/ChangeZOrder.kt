package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource

class ChangeZOrder(
        private val actorResource: ActorResource,
        private val newZOrder: Double)
    : Change {

    private val oldZOrder = actorResource.zOrder

    override fun redo(sceneEditor: SceneEditor) {
        actorResource.zOrder = newZOrder
        updateForm(sceneEditor)
    }

    override fun undo(sceneEditor: SceneEditor) {
        actorResource.zOrder = oldZOrder
        updateForm(sceneEditor)
    }

    /**
     * Update the actor attributes box, if it showing this actor resource's attributes.
     */
    private fun updateForm(sceneEditor: SceneEditor) {
        if (sceneEditor.actorAttributesBox.actorResource == actorResource) {
            sceneEditor.actorAttributesBox.actorAttributesForm?.zOrderP?.value = actorResource.zOrder
        }
    }
}
