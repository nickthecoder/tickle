package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor

interface Change {

    fun undo(sceneEditor: SceneEditor)

    fun redo(sceneEditor: SceneEditor)

    fun mergeWith(other: Change): Boolean = false
}
