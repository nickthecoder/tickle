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

class Resize(
        private val actorResource: DesignActorResource,
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