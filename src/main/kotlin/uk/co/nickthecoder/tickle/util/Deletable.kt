package uk.co.nickthecoder.tickle.util

interface Deletable {

    /**
     * Return null if the resource can be deleted, otherwise return a resource that depends upon it.
     * e.g. Texture will return a Pose, which prevents the texture being deleted.
     */
    fun usedBy(): Any?

    fun delete()

}
