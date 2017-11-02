package uk.co.nickthecoder.tickle.collision

import uk.co.nickthecoder.tickle.Actor

/**
 * A very simplistic overlapping test based on the bounding rectangle of the actors.
 * This works very badly for rotated actors (especially if they are long and narrow).
 * It works well (and very quickly) for rectangular objects rotated by 0, 90, 180, 270 degrees.
 */
class BoundingRectangleOverlapping : Overlapping {
    override fun overlapping(actorA: Actor, actorB: Actor): Boolean {

        val worldA = actorA.appearance.worldRect()
        val worldB = actorB.appearance.worldRect()

        return worldA.right > worldB.left && worldA.top > worldB.bottom && worldA.left < worldB.right && worldA.bottom < worldB.top
    }

}
