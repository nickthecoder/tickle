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
