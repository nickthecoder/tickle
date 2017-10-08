package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.action.movement.polar.Circle
import uk.co.nickthecoder.tickle.action.movement.polar.MovePolar
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Polar2f

class Coin() : ActionRole() {

    @Attribute(AttributeType.POLAR, scale = 10f)
    var velocity = Polar2f(Angle(), 2f)

    var turningSpeed = Angle.degrees(3.0)

    @CostumeAttribute
    var value: Int = 1

    override fun activated() {


        val growShrink = (Grow(actor, 1f, 2f).then(Grow(actor, 1f, 1f)).forever())
        val circle = Circle(velocity.angle, turningSpeed).and(MovePolar(actor.position, velocity))
        action = growShrink.and(circle)

        if (value < 10) {
            actor.color = Color(0.6f, 0.6f, 0.4f, 1f)

        } else if (value >= 10 && value < 100) {
            actor.color = Color(0.7f, 0.7f, 1f, 1f)
        }
    }

}
