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

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource
import uk.co.nickthecoder.tickle.editor.resources.ModificationType
import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.editor.util.PolarParameter

class ChangePolarParameter(
        private val actorResource: DesignActorResource,
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
