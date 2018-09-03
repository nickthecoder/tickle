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
package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.action.animation.Turn
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Polar2d

class ShootingStar() : ActionRole() {

    @Attribute(AttributeType.POLAR)
    val headingAndLength = Polar2d(Angle.degrees(15.0), 400.0)

    @Attribute
    val curveAngle = Angle.degrees(60.0)

    @Attribute
    val turnAngle = Angle.degrees(190.0)

    @Attribute
    var sideDuration: Double = 1.0

    override fun createAction(): Action {

        val growShrink = (Scale(actor, 1.0, 2.0).then(Scale(actor, 1.0, 1.0)).forever())

        val edge = (Forwards(actor.position, headingAndLength.magnitude, headingAndLength.angle, sideDuration, Eases.easeInOutExpo)
                .and(Turn(headingAndLength.angle, curveAngle, sideDuration, Eases.easeInOutExpo)))

                .then(Turn(headingAndLength.angle, turnAngle, 0.0))
                .then { actor.event("changeDirection") }

        return growShrink.and(edge.forever())
    }

}
