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
package uk.co.nickthecoder.tickle.action.animation

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.util.Angle

class Forwards(
        val position: Vector2d,
        val by: Double,
        val heading: Angle,
        seconds: Double,
        ease: Ease)

    : AnimationAction(seconds, ease) {

    constructor(position: Vector2d, by: Double, heading: Angle, seconds: Double) : this(position, by, heading, seconds, LinearEase.instance)

    override fun storeInitialValue() {
    }

    override fun update(t: Double) {
        position.add(heading.vector().mul(by * delta(t)))
    }
}
