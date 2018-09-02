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
package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2d

open class LimitSpeed(
        val velocity: Polar2d,
        var maxSpeed: Double = 10.0,
        var minSpeed: Double = 0.0)

    : Action {

    override fun act(): Boolean {
        if (velocity.magnitude < minSpeed) {
            velocity.magnitude = minSpeed
        }
        if (velocity.magnitude > maxSpeed) {
            velocity.magnitude = maxSpeed
        }
        return false
    }

}
