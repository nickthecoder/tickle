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


class Batch {

    val changes = mutableListOf<Change>()

    fun undo(sceneEditor: SceneEditor) {
        changes.reversed().forEach { it.undo(sceneEditor) }
    }

    fun redo(sceneEditor: SceneEditor) {
        changes.forEach { it.redo(sceneEditor) }
    }

    fun makeChange(sceneEditor: SceneEditor, change: Change) {
        change.redo(sceneEditor)
        changes.forEach { existingChange ->
            if (change.mergeWith(existingChange)) {
                return
            }
        }
        changes.add(change)
    }

    override fun toString(): String {
        return "Batch {\n${changes.joinToString(separator = "\n")}\n}"
    }

}
