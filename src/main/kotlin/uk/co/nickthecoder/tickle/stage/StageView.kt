package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Appearance
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.resources.ActorResource

interface StageView : View {

    var stage: Stage

    var centerX: Double

    var centerY: Double

    /**
     * Used by the SceneEditor to draw the Actors in the correct order.
     */
    fun orderActors(actorResources: List<ActorResource>, topFirst: Boolean): Iterable<ActorResource>

    fun orderedActors(topFirst: Boolean): Iterable<Actor>

    /**
     * Returns the Actors touching the given [position], the top-most first.
     */
    fun findActorsAt(position: Vector2d): Iterable<Actor> {
        return orderedActors(true).filter { it.touching(position) }
    }

    /**
     * Returns the top-most Actor at the given [position], or null, if there are no Actors
     * touching the position.
     *
     * Uses [Appearance.touching]. See [Appearance.pixelTouching] for details on how to change the threshold.
     */
    fun findActorAt(position: Vector2d): Actor? {
        return orderedActors(true).firstOrNull { it.touching(position) }
    }

}

inline fun <reified T : Role> StageView.findRoleAt(point: Vector2d): T? {
    return findRolesAt<T>(point).firstOrNull()
}

inline fun <reified T : Role> StageView.findRolesAt(point: Vector2d): List<T> {
    return orderedActors(true).map { it.role }.filterIsInstance<T>().filter { it.actor.touching(point) }
}
