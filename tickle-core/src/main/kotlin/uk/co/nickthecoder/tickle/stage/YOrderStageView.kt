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

import uk.co.nickthecoder.tickle.ActorDetails

class YOrderComparator : Comparator<ActorDetails> {
    override fun compare(o1: ActorDetails, o2: ActorDetails): Int = Math.signum(o1.y - o2.y).toInt()
}

/**
 * Orders Actors using their Y value only, use this for isometric games, where all Actors are touching the ground.
 * If you have above ground objects (flying objects or stacked objects), this simplistic view will NOT work correctly.
 * Consider using [YMinusZStageView] instead.
 *
 * Note, the Actor's zOrder is completely ignored.
 *
 * Note the "Offsets" of the Pose should be on the ground, at the center of the object.
 * For people, use the point between their feet (or the position of the grounded foot when walking/running).
 * For buildings use the middle of the building at ground level.
 * Do NOT use the center of the Pose's image as the Pose's offsets!
 */
class YOrderStageView
    : AbstractStageView(YOrderComparator()) {
}
