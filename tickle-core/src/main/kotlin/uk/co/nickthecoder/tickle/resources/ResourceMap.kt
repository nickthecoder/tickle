package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.editor.util.ResourceType

open class ResourceMap<T : Any>(val resources: Resources, val resourceType : ResourceType) {

    private val items = mutableMapOf<String, T>()

    fun items(): Map<String, T> = items

    fun find(name: String): T? {
        return items[name]
    }

    fun findName(item: T?): String? {
        if (item == null) return null
        return items.filter { entry -> entry.value === item }.map { it.key }.firstOrNull()
    }

    fun add(name: String, item: T) {
        items[name] = item
        resources.fireAdded(item, name)
    }

    fun remove(name: String) {
        items[name]?.let {
            items.remove(name)
            resources.fireRemoved(it, name)
        }
    }

    fun remove(resource: T) {
        findName(resource)?.let { remove(it) }
    }

    fun rename(resource: T, newName: String) {
        findName(resource)?.let { rename(it, newName) }
    }

    fun rename(oldName: String, newName: String) {
        items[oldName]?.let { item ->
            items.remove(oldName)
            items[newName] = item
            resources.fireRenamed(item, oldName, newName)
        }
    }

}
