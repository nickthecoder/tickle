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
package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.rotate

/**
 * Follows another position
 */
class Follow(
        val follower: Vector2d,
        val following: Vector2d,
        val offset: Vector2d,
        val rotation: Angle?)

    : Action {

    constructor(
            follower: Vector2d,
            following: Vector2d,
            offset: Vector2d
    ) : this(follower, following, offset, null)

    constructor(
            follower: Vector2d,
            actor: Actor,
            offset: Vector2d
    ) : this(follower, actor.position, offset, actor.direction)

    var tmp = Vector2d()

    override fun act(): Boolean {
        if (rotation != null) {
            tmp.set(offset)
            tmp.rotate(rotation)
            following.add(tmp, follower)
        } else {
            following.add(offset, follower)
        }
        return false
    }

}
