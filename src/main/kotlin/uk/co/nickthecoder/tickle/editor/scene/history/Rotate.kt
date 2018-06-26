package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

class Rotate(
        private val actorResource: ActorResource,
        private var newDegrees: Double)

    : Change {

    private val oldDegrees = actorResource.direction.degrees

    override fun redo(sceneEditor: SceneEditor) {
        actorResource.direction.degrees = newDegrees
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        actorResource.direction.degrees = oldDegrees
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is Rotate && other.actorResource == actorResource) {
            other.newDegrees = newDegrees
            return true
        }
        return false
    }
}