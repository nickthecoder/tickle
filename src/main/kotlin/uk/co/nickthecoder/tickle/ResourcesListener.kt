package uk.co.nickthecoder.tickle

interface ResourcesListener {

    fun changed(resource: Any) {}

    fun added(resource: Any, name: String) {}

    fun removed(resource: Any, name: String) {}

    fun renamed(resource: Any, oldName: String, newName: String) {}

}
