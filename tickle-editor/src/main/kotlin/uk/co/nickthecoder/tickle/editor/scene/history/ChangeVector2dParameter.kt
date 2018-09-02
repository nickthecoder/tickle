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
