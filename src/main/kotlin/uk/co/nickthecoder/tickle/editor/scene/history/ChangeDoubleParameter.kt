package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType

class ChangeDoubleParameter(
        private val actorResource: ActorResource,
        private val parameter: DoubleParameter,
        private val newValue: Double)
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

}
