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

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor
import uk.co.nickthecoder.tickle.resources.ActorResource

class ChangeZOrder(
        private val actorResource: ActorResource,
        private val newZOrder: Double)
    : Change {

    private val oldZOrder = actorResource.zOrder

    override fun redo(sceneEditor: SceneEditor) {
        actorResource.zOrder = newZOrder
        updateForm(sceneEditor)
    }

    override fun undo(sceneEditor: SceneEditor) {
        actorResource.zOrder = oldZOrder
        updateForm(sceneEditor)
    }

    /**
     * Update the actor attributes box, if it showing this actor resource's attributes.
     */
    private fun updateForm(sceneEditor: SceneEditor) {
        if (sceneEditor.actorAttributesBox.actorResource == actorResource) {
            sceneEditor.actorAttributesBox.actorAttributesForm?.zOrderP?.value = actorResource.zOrder
        }
    }
}
