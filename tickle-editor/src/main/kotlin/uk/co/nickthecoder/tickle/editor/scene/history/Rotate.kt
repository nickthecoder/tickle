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

class Rotate(
        private val actorResource: DesignActorResource,
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