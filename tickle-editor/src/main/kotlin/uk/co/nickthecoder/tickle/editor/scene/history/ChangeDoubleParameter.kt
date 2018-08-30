package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor

class ChangeDoubleParameter(
        private val actorResource: DesignActorResource,
        private val parameter: DoubleParameter,
        private var newValue: Double)
    : Change {

    private val oldValue = parameter.value

    override fun redo(sceneEditor: SceneEditor) {
        parameter.value = newValue
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        parameter.value = oldValue
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }


    override fun mergeWith(other: Change): Boolean {
        if (other is ChangeDoubleParameter && other.actorResource == actorResource) {
            other.newValue = newValue
            return true
        }
        return false
    }
}
