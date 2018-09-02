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

/**
 * Reduces the speed by applying a constant scaling of (1-drag).
 */
open class DragPolar(
        val velocity: Polar2d,
        drag: Double)

    : Action {

    var oneMinusDrag: Double = 1 - drag

    var drag: Double
        get() = 1 - oneMinusDrag
        set(v) {
            oneMinusDrag = 1 - v
        }

    override fun act(): Boolean {
        velocity.magnitude *= oneMinusDrag
        return false
    }

}
