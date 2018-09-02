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
import uk.co.nickthecoder.tickle.editor.scene.StageLayer

class MoveToLayer(
        private val actorResource: DesignActorResource,
        private val newLayer: StageLayer)
    : Change {

    private val oldLayer = actorResource.layer

    override fun redo(sceneEditor: SceneEditor) {
        sceneEditor.delete(actorResource)
        newLayer.stageResource.actorResources.add(actorResource)
        actorResource.layer = newLayer

        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.NEW)
    }

    override fun undo(sceneEditor: SceneEditor) {
        sceneEditor.delete(actorResource)
        if (oldLayer != null) {
            oldLayer.stageResource.actorResources.add(actorResource)
            actorResource.layer = oldLayer
        }
        sceneEditor.sceneResource.fireChange(actorResource, ModificationType.NEW)
    }
}
