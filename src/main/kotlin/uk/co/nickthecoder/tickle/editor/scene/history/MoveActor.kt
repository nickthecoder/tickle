package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

/**
 * Note. Unlike most Undo/Redo operations, the change has ALREADY occurred when this object is created.
 * This is needed, because of how snapping works.
 * So the actor is moved, then a MoveActor is created, and added to the history.
 */
class MoveActor(
        private val actorResource: ActorResource,
        private val oldX: Double,
        private val oldY: Double
) : Change {

    private var newX = actorResource.x

    private var newY = actorResource.y

    override fun redo(sceneEditor: SceneEditor) {
        actorResource.x = newX
        actorResource.y = newY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        actorResource.x = oldX
        actorResource.y = oldY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    /**
     * While dragging an actor, this allow all of the small increments to be merged into a single Change
     */
    override fun mergeWith(other: Change): Boolean {
        if (other is MoveActor && other.actorResource == actorResource) {
            other.newX = actorResource.x
            other.newY = actorResource.y
            return true
        }
        return false
    }
}
