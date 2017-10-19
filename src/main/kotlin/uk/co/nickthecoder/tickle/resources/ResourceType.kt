package uk.co.nickthecoder.tickle.resources

class ResourceType<T : Any>(val resources: Resources, val typeLabel: String) {

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

    fun delete(name: String) {
        items[name]?.let {
            items.remove(name)
            resources.fireRemoved(it, name)
        }
    }

    fun rename(oldName: String, newName: String) {
        items[oldName]?.let { item ->
            items.remove(oldName)
            items[newName] = item
            resources.fireRenamed(item, oldName, newName)
        }
    }

}
