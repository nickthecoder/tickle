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

class Scale(
        private val actorResource: DesignActorResource,
        private val oldX: Double,
        private val oldY: Double,
        private val oldScaleX: Double,
        private val oldScaleY: Double

) : Change {

    private var newX = actorResource.x
    private var newY = actorResource.y
    private var newScaleX = actorResource.scale.x
    private var newScaleY = actorResource.scale.y

    override fun redo(sceneEditor: SceneEditor) {
        actorResource.x = newX
        actorResource.y = newY
        actorResource.scale.x = newScaleX
        actorResource.scale.y = newScaleY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun undo(sceneEditor: SceneEditor) {
        actorResource.x = oldX
        actorResource.y = oldY
        actorResource.scale.x = oldScaleX
        actorResource.scale.y = oldScaleY
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
    }

    override fun mergeWith(other: Change): Boolean {
        if (other is Scale && other.actorResource == actorResource) {
            other.newX = newX
            other.newY = newY
            other.newScaleX = newScaleX
            other.newScaleY = newScaleY
            return true
        }
        return false
    }

}
