package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.util.distanceSquared

interface Role {

    var actor: Actor

    /**
     * Called when the Actor and the role are both complete, and the Actor has been placed on a Stage.
     * Note, when starting a new scene, all of the Actors' Role's begin() methods will be called before any
     * of the Role's activated() method is called.
     * For Actor created dynamically during a game, activated() will be called immediately after begin().
     * tick() events will not happen before both begin() and activated().
     */
    fun begin()

    /**
     * When starting a new scene, all of the Actors' Role's begin() methods are called before any Role's
     * activated method is called.
     * tick() events will not happen before both begin() and activated().
     */
    fun activated()

    /**
     * Called once per frame from inside the main game loop.
     * Do NOT perform long operations in the tick() method, as this will lead to dropped frames.
     * If you need to perform long calculations, consider using a new Thread, Path finding is a good example of
     * code that should NOT be calculated in the tick() method.
     */
    fun tick()

    /**
     * Signals that the Actor has died, and has been removed from the Stage. Do not attempt to revive a dead Actor, by
     * placing it on a new Stage. Weirdness may ensue.
     */
    fun end()

    /**
     * Finds the closest role from the list, or null if the list is empty (or only contains this).
     * If the list contains 'this', then 'this' is ignored (this will never be returned).
     */
    fun closest(roles: Iterable<Role>): Role? {
        var closest: Role? = null
        var closestD2 = Double.MAX_VALUE
        roles.forEach { other ->
            if (this !== other) {
                val d2 = actor.position.distanceSquared(other.actor.position)
                if (d2 < closestD2) {
                    closestD2 = d2
                    closest = other
                }
            }
        }
        return closest
    }

    companion object {

        fun create(roleString: String): Role? {
            try {
                val klass = Class.forName(roleString)
                val newRole = klass.newInstance()
                if (newRole is Role) {
                    return newRole
                } else {
                    System.err.println("'$roleString' is not a type of Role")
                }
            } catch (e: Exception) {
                System.err.println(e)
                System.err.println("Failed to create a Role from : '$roleString'")
            }
            return null
        }
    }
}