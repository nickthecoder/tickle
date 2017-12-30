package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role

interface FindRolesStrategy {

    fun <T : Role> findRoles(stage: Stage, type: Class<T>): List<T>

    fun add(actor: Actor)

    fun remove(actor: Actor)
}

class SlowFindRolesStrategy : FindRolesStrategy {

    override fun <T : Role> findRoles(stage: Stage, type: Class<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return stage.actors.filter { type.isInstance(it.role) }.map { it.role as T }
    }

    override fun add(actor: Actor) {}

    override fun remove(actor: Actor) {}

    companion object {
        val instance = SlowFindRolesStrategy()
    }
}

/**
 * Optimises calls to [Stage.findRoles] by keeping a cache of roles keyed by class.
 *
 * Initially the cache is empty, then when findRoles is called, the slow implementation is used to fill the cache
 * for that particular class.
 *
 * Whenever an actor is added to the stage, if its role is an instance of a cached class, then the actor's role
 * is added to the cached list.
 *
 * Whenever an actor is remove from the stage, it is removed from all cached lists.
 *
 * Note, a single actor's role can be in multiple lists. For example, findRoles<Ball> followed by findRoles<BlueBall>
 * will create two lists (one for Ball and one for BlueBall). If BlueBall is a subclass of Ball, then Actors with a
 * BlueBall Role will be in both lists.
 *
 * The net result is a small penalty when adding/removing actors, and a large improvement when finding roles.
 */
open class CachedFindRolesStrategy : FindRolesStrategy {

    val actorsByType = mutableMapOf<Class<*>, MutableList<Role>>()

    override fun <T : Role> findRoles(stage: Stage, type: Class<T>): List<T> {
        val cached = actorsByType[type]
        if (cached == null) {
            val result = SlowFindRolesStrategy.instance.findRoles(stage, type)
            actorsByType[type] = (result as List<Role>).toMutableList()
            return result

        } else {
            @Suppress("UNCHECKED_CAST")
            return cached as List<T>
        }

    }

    override fun add(actor: Actor) {
        actor.role?.let { role ->
            actorsByType.forEach { type, list ->
                if (type.isInstance(actor.role)) {
                    list.add(role)
                }
            }
        }
    }

    override fun remove(actor: Actor) {
        actorsByType.values.forEach { it.remove(actor.role) }
    }

}

/**
 * A subclass of [GameStage], which optimises [findRoles] using a [FindRolesStrategy].
 *
 * The default strategy is [CachedFindRolesStrategy], because this is generic enough to work for any game.
 *
 * However, you could implement your own, customised strategy optimised specifically your specific game.
 * For example, if you never find roles which have subclasses, you don't need the isInstance calls required by
 * CachedFindRolesStrategy, and use "===" instead.
 */
open class OptimisedStage : GameStage() {

    var findRolesStrategy: FindRolesStrategy = CachedFindRolesStrategy()
        set(v) {
            field = v
            actors.forEach { v.add(it) }
        }

    override fun <T : Role> findRoles(type: Class<T>): List<T> {
        return findRolesStrategy.findRoles(this, type)
    }

    override fun add(actor: Actor, activate: Boolean) {
        super.add(actor, activate)
        findRolesStrategy.add(actor)
    }

    override fun remove(actor: Actor) {
        super.remove(actor)
        findRolesStrategy.remove(actor)
    }

}
