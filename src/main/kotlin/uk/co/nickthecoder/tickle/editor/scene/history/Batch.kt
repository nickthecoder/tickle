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
        val last = changes.lastOrNull()
        if (last != null && change.mergeWith(last)) {
            // Do nothing
        } else {
            changes.add(change)
        }
    }

    override fun toString(): String {
        return "Batch {\n${changes.joinToString(separator = "\n")}\n}"
    }

}
