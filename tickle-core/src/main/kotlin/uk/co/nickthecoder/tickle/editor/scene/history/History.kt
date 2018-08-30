package uk.co.nickthecoder.tickle.editor.scene.history

import uk.co.nickthecoder.tickle.editor.scene.SceneEditor

/**
 * All modifications to the document happen through History.
 * Instead of changing the document, and then noting that change with [History],
 * we tell [History] of the changes to be made.
 * [History] will perform the modification, and add the modification to its list.
 *
 * The history is arranged in batches, where each batch is the smallest item that can be
 * undone or redone.
 * To modify the document, begin a batch with [beginBatch], then modify the document via [makeChange], and
 * finally end the batch with [endBatch]. If you wish to abandon a batch instead of ending it, use [abandonBatch].
 *
 * The document will be modified
 */
class History(private val sceneEditor: SceneEditor) {

    private val history = mutableListOf<Batch>()

    /**
     * When adding
     */
    private var currentBatch: Batch? = null

    /**
     * The index into [history] where new [Batch]es will be added.
     * If the index == 0, then there is no more to undo.
     * If the index == history.size, then there is nothing to redo.
     */
    private var currentIndex = 0

    /**
     * The index when the document was saved.
     * If currentIndex != savedIndex, then the document has changed to the version on disk.
     */
    private var savedIndex = 0

    /**
     * Set to true during undo and redo so that ParameterListeners don't cause ANOTHER Change to be added to the history.
     */
    var updating = false

    fun canUndo() = currentIndex > 0 || currentBatch != null

    fun canRedo() = currentIndex < history.size

    fun clear() {
        savedIndex = -1
        currentIndex = 0
        currentBatch = null
        history.clear()
    }

    fun undo() {
        if (canUndo()) {
            updating = true
            if (currentBatch == null) {
                currentIndex--
                val batch = history[currentIndex]
                batch.undo(sceneEditor)
            } else {
                abandonBatch()
            }
            updating = false
        }
    }

    fun redo() {
        if (canRedo()) {
            updating = true
            val batch = history[currentIndex]
            currentIndex++
            batch.redo(sceneEditor)
            updating = false
        }
    }

    fun beginBatch() {
        currentBatch = Batch()
    }

    fun abandonBatch() {
        currentBatch?.undo(sceneEditor)
        currentBatch = null
    }

    fun endBatch() {
        currentBatch?.let {
            if (!it.changes.isEmpty()) {
                if (savedIndex > currentIndex) {
                    // We've destroyed the redo that would take us back to the saved state.
                    savedIndex = -1
                }

                while (history.size > currentIndex) {
                    history.removeAt(history.size - 1)
                }
                history.add(currentIndex, it)
                currentIndex++
            }
            currentBatch = null
        }
    }

    fun makeChange(change: Change) {
        currentBatch?.let {
            it.makeChange(sceneEditor, change)
            return
        }
        beginBatch()
        makeChange(change)
        endBatch()
    }


    fun isSavedVersion() = savedIndex == currentIndex

    fun saved() {
        savedIndex = currentIndex
    }

    /**
     * Prints out all the changes while performing and "undo".
     */
    fun debugUndo() {
        if (canUndo()) {
            if (currentBatch == null) {
                currentIndex--
                val batch = history[currentIndex]
                println("Undoing a batch")
                println(batch)
                batch.undo(sceneEditor)
            } else {
                println("Abandoning the current batch...")
                println(currentBatch)
                abandonBatch()
            }
        }
    }
}
