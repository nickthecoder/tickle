package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.action.movement.polar.MovePolar
import uk.co.nickthecoder.tickle.action.movement.polar.Turn
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Scalar

class Coin() : ActionRole() {

    @Attribute
    var initialSpeed: Float = 2f

    var speed = Scalar(initialSpeed)

    @Attribute(AttributeType.DIRECTION, 1)
    var initialHeading: Double = 0.0

    var heading = Angle()

    @Attribute
    var turningSpeed: Double = 3.0

    @CostumeAttribute
    var value: Int = 1

    override fun activated() {

        heading.degrees = initialHeading

        val growShrink = (Grow(actor, 1f, 2f).then(Grow(actor, 1f, 1f)).forever())
        val circle = Turn<Actor>(heading, turningSpeed).and(MovePolar(actor.position, heading, speed))
        action = growShrink.and(circle)

        if (value < 10) {
            actor.color = Color(0.6f, 0.6f, 0.4f, 1f)

        } else if (value >= 10 && value < 100) {
            actor.color = Color(0.7f, 0.7f, 1f, 1f)
        }
    }

}
