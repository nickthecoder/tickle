package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.ActorDetails
import uk.co.nickthecoder.tickle.Appearance
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.editor.util.sortedBackwardsWith

interface StageView : View {

    var stage: Stage

    var centerX: Double

    var centerY: Double

    val comparator: Comparator<ActorDetails>

    val roleComparator: Comparator<Role>

    /**
     * Returns the Actors touching the given [position], the top-most first.true
     */
    fun findActorsAt(point: Vector2d): Iterable<Actor> {
        return topFirst(stage.findActorsAt(point))
    }

    /**
     * Returns the top-most Actor at the given [position], or null, if there are no Actors
     * touching the position.
     *
     * Uses [Appearance.touching]. See [Appearance.pixelTouching] for details on how to change the threshold.
     */
    fun findActorAt(point: Vector2d): Actor? {
        return findActorsAt(point).firstOrNull()
    }

    fun topFirst(actors: Collection<Actor>) = actors.sortedBackwardsWith(comparator)

    fun bottomFirst(actors: Iterable<Actor>) = actors.sortedWith(comparator)
}

inline fun <reified T : Role> StageView.findRoleAt(point: Vector2d): T? {
    return findRolesAt<T>(point).firstOrNull()
}

inline fun <reified T : Role> StageView.findRolesAt(point: Vector2d): List<T> {
    return stage.findRolesAt<T>(point).sortedBackwardsWith(roleComparator)
}
