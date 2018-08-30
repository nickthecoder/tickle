package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter

class ChangeVector2dParameter(
        private val actorResource: DesignActorResource,
        private val parameter: Vector2dParameter,
        private var newX: Double,
        private var newY: Double
) : Change {

    private val oldX = parameter.x ?: 0.0
    private val oldY = parameter.y ?: 0.0

    override fun redo(sceneEditor: SceneEditor) {
        parameter.x = newX
        parameter.y = newY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        parameter.x = oldX
        parameter.y = oldY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is ChangeVector2dParameter && other.actorResource == actorResource) {
            other.newX = newX
            other.newY = newY
            return true
        }
        return false
    }
}
