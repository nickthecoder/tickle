package uk.co.nickthecoder.tickle

interface ResourcesListener {

    fun resourceChanged(resource: Any) {}

    fun resourceAdded(resource: Any, name: String) {}

    fun resourceRemoved(resource: Any, name: String) {}

    fun resourceRenamed(resource: Any, oldName: String, newName: String) {}

}
