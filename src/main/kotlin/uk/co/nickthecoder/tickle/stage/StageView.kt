package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
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
    fun actorsAt(position: Vector2d): Iterable<Actor> {
        return orderedActors(true).filter { it.touching(position) }
    }

    /**
     * Returns the top-most Actor at the given [position], or null, if there are no Actors
     * touching the position.
     */
    fun actorAt(position: Vector2d): Actor? {
        return orderedActors(true).firstOrNull { it.touching(position) }
    }

}
