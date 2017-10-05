package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.tickle.SceneActor

class Selection : Iterable<SceneActor> {

    private val items = mutableSetOf<SceneActor>()

    private var latest: SceneActor? = null

    val listeners = mutableListOf<SelectionListener>()
    val size
        get() = items.size


    fun isEmpty() = items.isEmpty()

    fun isNotEmpty() = items.isNotEmpty()

    fun clear() {
        items.clear()
        latest = null
        fireChange()
    }

    fun add(obj: SceneActor?) {
        obj ?: return
        items.add(obj)
        latest = obj
        fireChange()
    }

    fun remove(obj: SceneActor?) {
        obj ?: return
        items.remove(obj)
        if (obj === latest) {
            latest = null
        }
        fireChange()
    }

    fun selected(): Set<SceneActor> = items

    fun latest(): SceneActor? = latest

    fun clearAndSelect(obj: SceneActor?) {
        clear()
        obj?.let { add(it) }
        fireChange()
    }

    fun fireChange() {
        listeners.forEach { it.selectionChanged() }
    }

    override fun iterator(): Iterator<SceneActor> = items.iterator()

}

interface SelectionListener {

    fun selectionChanged()

}
