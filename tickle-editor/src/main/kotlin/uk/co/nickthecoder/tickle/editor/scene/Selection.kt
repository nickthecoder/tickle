package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.tickle.editor.resources.DesignActorResource

class Selection : Iterable<DesignActorResource> {

    private val items = mutableSetOf<DesignActorResource>()

    private var latest: DesignActorResource? = null

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

    fun add(obj: DesignActorResource?) {
        obj ?: return
        items.add(obj)
        latest = obj
        fireChange()
    }

    fun remove(obj: DesignActorResource?) {
        obj ?: return
        items.remove(obj)
        if (obj === latest) {
            latest = null
        }
        fireChange()
    }

    fun selected(): Set<DesignActorResource> = items

    fun latest(): DesignActorResource? = latest

    fun clearAndSelect(obj: DesignActorResource?) {
        clear()
        obj?.let { add(it) }
        fireChange()
    }

    fun fireChange() {
        listeners.forEach { it.selectionChanged() }
    }

    override fun iterator(): Iterator<DesignActorResource> = items.iterator()

}

interface SelectionListener {

    fun selectionChanged()

}
