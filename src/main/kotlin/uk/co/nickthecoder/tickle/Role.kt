package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.NoAction

interface Role {

    var actor: Actor

    /**
     * Called when the Actor and the role are both complete, and the Actor has been placed on a Stage.
     * Note, when starting a new scene, all of the Actors' Role's begin() methods will be called before any
     * of the Role's activated() method is called.
     * For Actor created dynamically during a game, activated() will be called immediately after begin().
     * tick() events will not happen before both begin() and activated().
     */
    fun begin() {}

    /**
     * When starting a new scene, all of the Actors' Role's begin() methods are called before any Role's
     * activated method is called.
     * tick() events will not happen before both begin() and activated().
     */
    fun activated() {}

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
    fun end() {}

    /**
     * Finds the closest role from the list, or null if the list is empty (or only contains this).
     * If the list contains 'this', then 'this' is ignored (this will never be returned).
     */
    fun closest(roles: Iterable<Role>): Role? {
        var closest: Role? = null
        var closestD2 = Float.MAX_VALUE
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

}

abstract class AbstractRole : Role {

    override lateinit var actor: Actor

    override fun toString() = javaClass.simpleName

}

/**
 * A Role that only has a single Action, and does nothing in the tick method itself.
 * If 'die' is true, then the Actor will be automatically killed when the Action ends.
 */
class ActionRole(action: Action<Actor>, die: Boolean = true) : Role {

    val action = if (die) action else action.then(NoAction())

    override lateinit var actor: Actor

    override fun activated() {
        super.activated()
        action.begin(actor)
    }

    override fun tick() {
        if (action.act(actor)) {
            actor.die()
        }
    }
}
