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
package uk.co.nickthecoder.tickle.editor.resources

import uk.co.nickthecoder.tickle.editor.scene.SnapRotation
import uk.co.nickthecoder.tickle.editor.scene.SnapToGrid
import uk.co.nickthecoder.tickle.editor.scene.SnapToGuides
import uk.co.nickthecoder.tickle.editor.scene.SnapToOthers
import uk.co.nickthecoder.tickle.resources.SceneResource

class DesignSceneResource : SceneResource() {

    val snapToGrid = SnapToGrid()

    val snapToGuides = SnapToGuides()

    val snapToOthers = SnapToOthers()

    val snapRotation = SnapRotation()

    val listeners = mutableSetOf<SceneResourceListener>()

    fun fireChange(actorResource: DesignActorResource, type: ModificationType) {
        listeners.toList().forEach {
            it.actorModified(this, actorResource, type)
        }
    }
}
