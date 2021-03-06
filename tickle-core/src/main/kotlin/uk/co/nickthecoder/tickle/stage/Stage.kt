/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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

    fun add(actor: Actor)

    fun remove(actor: Actor)

    fun addView(view: StageView)

    fun firstView(): StageView? = views.firstOrNull()

    fun findActorById(id: Int): Actor? {
        return actors.firstOrNull { it.id == id }
    }

    /**
     * Note, the default implementation is slow, as it iterates over all actors. If you call this often and/or the stage
     * has hundreds of Actors, consider creating a subclass, optimised using a Neighbourhood.
     */
    fun findActorsAt(point: Vector2d): List<Actor> {
        return actors.filter { it.touching(point) }
    }

    /**
     * Note, the default implementation is slow, as it iterates over all actors. If you call this often and/or the stage
     * has hundreds of Actors, consider creating a subclass, optimised using a Neighbourhood.
     */
    fun findActorAt(point: Vector2d): Actor? {
        return findActorsAt(point).firstOrNull()
    }

    /**
     * Note, the default implementation is slow, as it iterates over all actors. If you call this often and/or the stage
     * has hundreds of Actors, consider creating a subclass, with a map of the actors by their class.
     *
     * Do not call this directly, instead use the inline reified [findRoles]
     */
    fun <T : Role> findRolesByClass(type: Class<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return actors.filter { type.isInstance(it.role) }.map { it.role as T }
    }

    fun <T : Role> findRoleByClass(type: Class<T>): T? {
        return findRolesByClass(type).firstOrNull()
    }

    /**
     * Finds all roles at the given point of a given type.
     *
     * Note, the default implementation is slow, as it iterates over all actors. If you call this often and/or the stage
     * has hundreds of Actors, consider creating a subclass, optimised using a Neighbourhood.
     *
     * Do not call this directly, instead use the the inline reified [findRoles]
     */
    fun <T : Role> findRolesByClassAt(type: Class<T>, point: Vector2d): List<T> {
        return findRolesByClassAt(type, point).filter { it.actor.touching(point) }
    }
}

/**
 * An idiomatic version of findRoles(Class<T>)
 */
inline fun <reified T : Role> Stage.findRoles(): List<T> {
    return findRolesByClass(T::class.java).filterIsInstance<T>()
}

/**
 * An idiomatic version of findRole(Class<T>)
 */
inline fun <reified T : Role> Stage.findRole(): T? {
    return findRoles<T>().firstOrNull()
}

/**
 * An idiomatic version of findRolesAt(Class<T>, Vector2d)
 */
inline fun <reified T : Role> Stage.findRolesAt(point: Vector2d): List<T> {
    return findRoles<T>().filter { it.actor.touching(point) }
}

/**
 * An idiomatic version of findRoleAt(Class<T>, Vector2d)
 */
inline fun <reified T : Role> Stage.findRoleAt(point: Vector2d): T? {
    return findRolesAt<T>(point).firstOrNull()
}

