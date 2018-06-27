package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

/**
 * Added to History when a parameter on the ActorAttributesForm changes.
 * Note, unlike most Changes, these are created AFTER the change has occurred.
 */
class ChangedParameter<T> (
        private val actorResource: ActorResource,
        private val parameter: ValueParameter<T>,
        private val oldValue: T)
    : Change {

    private var newValue = parameter.value

    override fun redo(sceneEditor: SceneEditor) {
        if ( parameter.value != newValue ) {
            parameter.value = newValue
            sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        }
    }

    override fun undo(sceneEditor: SceneEditor) {
        parameter.value = oldValue
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    /*
    override fun mergeWith(other: Change): Boolean {
        if (other is ChangedParameter<*> && other.actorResource == actorResource) {
            other.newValue = newValue
            return true
        }
        return false
    }
    */
}
