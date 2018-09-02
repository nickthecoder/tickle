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

import uk.co.nickthecoder.tickle.util.Angle

class Turn(
        val heading: Angle,
        val angle: Angle,
        seconds: Double,
        ease: Ease = Eases.linear)

    : AnimationAction(seconds, ease) {

    private var initialRadians: Double = 0.0

    private var finalRadians: Double = 0.0

    override fun storeInitialValue() {
        initialRadians = heading.radians
        finalRadians = initialRadians + angle.radians
    }

    override fun update(t: Double) {
        heading.radians = lerp(initialRadians, finalRadians, t)
    }
}
