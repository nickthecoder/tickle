package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.physics.TickleWorld

interface Stage {

    val views: List<StageView>

    val actors: Set<Actor>

    var world: TickleWorld?

    fun begin()

    fun activated()

    fun end()

    /**
     * Calls tick on all of the Actors' Roles on this stage. Note that if the Stage has a TickleWorld, then do NOT
     * call world.tick from within this method. Instead, Scene will call stage.world.tick for all stages after
     * ALL of the stage's tick methods have been called. i.e. all the Roles' tick methods are called, then all
     * lf the worlds' tick methods.
     */
    fun tick()

    fun add(actor: Actor, activate: Boolean = true)

    fun remove(actor: Actor)

    fun addView(view: StageView)

    fun firstView(): StageView? = views.firstOrNull()

    fun findActorsAt(point: Vector2d): List<Actor> {
        return actors.filter { it.touching(point) }
    }

    fun findActorAt(point: Vector2d): Actor? {
        return findActorsAt(point).firstOrNull()
    }

}

inline fun <reified T : Role> Stage.findRole(): T? {
    return findRoles<T>().firstOrNull()
}

inline fun <reified T : Role> Stage.findRoles(): List<T> {
    return actors.filter { it.role is T }.map { it.role }.filterIsInstance<T>()
}


inline fun <reified T : Role> Stage.findRoleAt(point: Vector2d): T? {
    return findRolesAt<T>(point).firstOrNull()
}

inline fun <reified T : Role> Stage.findRolesAt(point: Vector2d): List<T> {
    return findRoles<T>().filter { it.actor.touching(point) }
}
