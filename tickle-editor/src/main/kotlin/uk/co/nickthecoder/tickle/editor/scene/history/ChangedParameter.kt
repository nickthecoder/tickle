/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor

/**
 * Added to History when a parameter on the ActorAttributesForm changes.
 * Note, unlike most Changes, these are created AFTER the change has occurred.
 */
class ChangedParameter<T> (
        private val actorResource: DesignActorResource,
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
