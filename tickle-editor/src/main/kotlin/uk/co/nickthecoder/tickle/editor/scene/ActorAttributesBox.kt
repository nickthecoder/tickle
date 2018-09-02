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
package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.Node
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource

class ActorAttributesBox(val sceneEditor: SceneEditor)
    : SelectionListener {

    val stack = StackPane()

    var actorAttributesForm: ActorAttributesForm? = null

    var actorResource: DesignActorResource?
        get() = actorAttributesForm?.actorResource
        set(v) {
            if (actorAttributesForm?.actorResource != v) {
                actorAttributesForm?.cleanUp()
                stack.children.clear()
                if (v != null) {
                    actorAttributesForm = ActorAttributesForm(sceneEditor, v, sceneEditor.sceneResource)
                    stack.children.add(actorAttributesForm!!.build())
                }
            }
        }

    init {
        sceneEditor.selection.listeners.add(this)
    }

    fun build(): Node {
        return stack
    }

    override fun selectionChanged() {
        actorResource = sceneEditor.selection.latest()
    }

}

