package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.tickle.resources.ActorResource

class Selection : Iterable<ActorResource> {

    private val items = mutableSetOf<ActorResource>()

    private var latest: ActorResource? = null

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

    fun add(obj: ActorResource?) {
        obj ?: return
        items.add(obj)
        latest = obj
        fireChange()
    }

    fun remove(obj: ActorResource?) {
        obj ?: return
        items.remove(obj)
        if (obj === latest) {
            latest = null
        }
        fireChange()
    }

    fun selected(): Set<ActorResource> = items

    fun latest(): ActorResource? = latest

    fun clearAndSelect(obj: ActorResource?) {
        clear()
        obj?.let { add(it) }
        fireChange()
    }

    fun fireChange() {
        listeners.forEach { it.selectionChanged() }
    }

    override fun iterator(): Iterator<ActorResource> = items.iterator()

}

interface SelectionListener {

    fun selectionChanged()

}
