package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Grow
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

        val growShrink = (Grow(actor, 1.0, 2.0).then(Grow(actor, 1.0, 1.0)).forever())
        val circle = Circle(velocity.angle, turningSpeed).and(MovePolar(actor.position, velocity))
        action = growShrink.and(circle)

        actor.color = color
        return action
    }

}
