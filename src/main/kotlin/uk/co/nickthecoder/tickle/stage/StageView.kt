package uk.co.nickthecoder.tickle.stage

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.util.sortedBackwardsWith
import uk.co.nickthecoder.tickle.resources.ActorXAlignment
import uk.co.nickthecoder.tickle.resources.ActorYAlignment
import uk.co.nickthecoder.tickle.util.Recti

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


    /**
     * Adjusts any actors who's position is not relative to the bottom left.
     * This is used by games with resizable windows, and actors need to be aligned with the right edge for example.
     *
     * This is automatically called whenever a Scene is laid out, via [Scene.layoutToFit], [Scene.layoutWithMargins] etc.
     */
    override fun changeRect(newRect: Recti) {

        val deltaX = newRect.width - rect.width
        val deltaY = newRect.height - rect.height

        if (deltaX != 0 || deltaY != 0) {
            val ratioX = newRect.width.toDouble() / rect.width
            val ratioY = newRect.height.toDouble() / rect.height

            stage.actors.forEach { actor ->

                when (actor.viewAlignmentX) {
                    ActorXAlignment.LEFT -> Unit // Do nothing
                    ActorXAlignment.CENTER -> actor.x += deltaX / 2
                    ActorXAlignment.RIGHT -> actor.x += deltaX
                    ActorXAlignment.RATIO -> actor.x *= ratioX
                }
                when (actor.viewAlignmentY) {
                    ActorYAlignment.BOTTOM -> Unit // Do nothing
                    ActorYAlignment.CENTER -> actor.y += deltaY / 2
                    ActorYAlignment.TOP -> actor.y += deltaY
                    ActorYAlignment.RATIO -> actor.y *= ratioY
                }
            }
        }

        super.changeRect(newRect)
    }

}

inline fun <reified T : Role> StageView.findRoleAt(point: Vector2d): T? {
    return findRolesAt<T>(point).firstOrNull()
}

inline fun <reified T : Role> StageView.findRolesAt(point: Vector2d): List<T> {
    return stage.findRolesAt<T>(point).sortedBackwardsWith(roleComparator)
}
