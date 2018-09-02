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

/**
 * Note. Unlike most Undo/Redo operations, the change has ALREADY occurred when this object is created.
 * This is needed, because of how snapping works.
 * So the actor is moved, then a MoveActor is created, and added to the history.
 */
class MoveActor(
        private val actorResource: DesignActorResource,
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
