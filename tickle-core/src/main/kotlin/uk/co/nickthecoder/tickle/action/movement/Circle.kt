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
import uk.co.nickthecoder.tickle.util.Angle


open class Circle(
        val position: Vector2d,
        val radius: Double,
        seconds: Double,
        val fromAngle: Double = 0.0,
        val toAngle: Double = Math.PI * 2,
        ease: Ease = LinearEase.instance,
        val radiusY: Double = radius)

    : AnimationAction(seconds, ease) {

    constructor(
            position: Vector2d,
            radius: Double,
            seconds: Double,
            fromAngle: Angle,
            toAngle: Angle,
            ease: Ease = LinearEase.instance,
            radiusY: Double = radius)

            : this(position, radius, seconds, fromAngle.radians, toAngle.radians, ease, radiusY)

    var center = Vector2d()

    override fun storeInitialValue() {
        center.set(position.x - Math.cos(fromAngle) * radius, position.y - Math.sin(fromAngle) * radiusY)
    }

    override fun update(t: Double) {
        val angle = lerp(fromAngle, toAngle, t)
        position.x = center.x + Math.cos(angle) * radius
        position.y = center.y + Math.sin(angle) * radiusY
    }

}
