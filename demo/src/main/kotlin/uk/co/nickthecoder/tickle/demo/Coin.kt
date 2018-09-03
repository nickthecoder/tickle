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
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.action.movement.polar.Circle
import uk.co.nickthecoder.tickle.action.movement.polar.MovePolar
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Polar2d

class Coin : ActionRole() {

    @Attribute(AttributeType.POLAR, scale = 10.0)
    var velocity = Polar2d(Angle(), 2.0)

    @Attribute
    var turningSpeed = Angle.degrees(3.0)

    @CostumeAttribute
    var value: Int = 1

    @CostumeAttribute(hasAlpha = false)
    var color: Color = Color.white()

    override fun createAction(): Action {

        val growShrink = (Scale(actor, 1.0, 2.0).then(Scale(actor, 1.0, 1.0)).forever())
        val circle = Circle(velocity.angle, turningSpeed).and(MovePolar(actor.position, velocity))
        action = growShrink.and(circle)

        actor.color = color
        return action
    }

}
