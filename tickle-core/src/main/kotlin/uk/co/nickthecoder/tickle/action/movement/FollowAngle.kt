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

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle

class FollowAngle(
        val follower: Angle,
        val following: Angle,
        val offsetRadians: Double = 0.0)

    : Action {

    constructor(
            follower: Angle,
            following: Angle,
            offset: Angle
    ) : this(follower, following, offset.radians)

    constructor(
            follower: Actor,
            following: Actor,
            offset: Angle
    ) : this(follower.direction, following.direction, offset.radians)

    constructor(
            follower: Actor,
            following: Actor
    ) : this(follower.direction, following.direction, 0.0)

    override fun act(): Boolean {
        follower.radians = following.radians + offsetRadians
        return false
    }
}