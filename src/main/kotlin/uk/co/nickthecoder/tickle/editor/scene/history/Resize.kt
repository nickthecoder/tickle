package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

class Resize(
        private val actorResource: ActorResource,
        private val oldX: Double,
        private val oldY: Double,
        private val oldSizeX: Double,
        private val oldSizeY: Double

) : Change {

    private var newX = actorResource.x
    private var newY = actorResource.y
    private var newSizeX = actorResource.size.x
    private var newSizeY = actorResource.size.y

    override fun redo(sceneEditor: SceneEditor) {
        actorResource.x = newX
        actorResource.y = newY
        actorResource.size.x = newSizeX
        actorResource.size.y = newSizeY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        actorResource.x = oldX
        actorResource.y = oldY
        actorResource.size.x = oldSizeX
        actorResource.size.y = oldSizeY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is Resize && other.actorResource == actorResource) {
            other.newX = newX
            other.newY = newY
            other.newSizeX = newSizeX
            other.newSizeY = newSizeY
            return true
        }
        return false
    }

}