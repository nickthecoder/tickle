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

    /**
     * Returns the Actors touching the given [position], the top-most first.true
     */
    fun findActorsAt(point: Vector2d): Iterable<Actor> {
        return stage.findActorsAt(point).sortedWith(comparator)
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

}

inline fun <reified T : Role> StageView.findRoleAt(point: Vector2d, topFirst: Boolean = true): T? {
    return findRolesAt<T>(point, topFirst).firstOrNull()
}

inline fun <reified T : Role> StageView.findRolesAt(point: Vector2d, topFirst: Boolean = true): List<T> {
    return if (topFirst) {
        stage.findActorsAt(point).filter { it.role is T }.sortedBackwardsWith(comparator).map { it.role }.filterIsInstance<T>()
    } else {
        stage.findActorsAt(point).filter { it.role is T }.sortedWith(comparator).map { it.role }.filterIsInstance<T>()
    }
}
