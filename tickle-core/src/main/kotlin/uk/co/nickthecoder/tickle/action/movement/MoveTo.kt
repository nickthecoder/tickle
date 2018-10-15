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
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp

open class MoveTo(
        val position: Vector2d,
        seconds: Double,
        val finalPosition: Vector2d,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialPosition = Vector2d()

    override fun storeInitialValue() {
        initialPosition.set(position)
    }

    override fun update(t: Double) {
        position.lerp(initialPosition, finalPosition, t)
    }
}
