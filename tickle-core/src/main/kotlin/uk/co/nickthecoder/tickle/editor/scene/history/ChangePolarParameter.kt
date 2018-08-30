package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.util.PolarParameter
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

class ChangePolarParameter(
        private val actorResource: ActorResource,
        private val parameter: PolarParameter,
        private var newDegrees: Double,
        private var newMagnitude: Double
) : Change {

    private val oldDegrees = parameter.angle ?: 0.0

    private val oldMagnitude = parameter.magnitude ?: 0.0

    override fun redo(sceneEditor: SceneEditor) {
        parameter.angle = newDegrees
        parameter.magnitude = newMagnitude
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        parameter.angle = oldDegrees
        parameter.magnitude = oldMagnitude
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is ChangePolarParameter && other.actorResource == actorResource) {
            other.newDegrees = newDegrees
            other.newMagnitude = newMagnitude
            return true
        }
        return false
    }

}
