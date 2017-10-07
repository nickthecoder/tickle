package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.action.movement.polar.Circle
import uk.co.nickthecoder.tickle.action.movement.polar.MovePolar
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.CostumeAttribute
import uk.co.nickthecoder.tickle.util.Polar2f

class Coin() : ActionRole() {

    var initialSpeed: Float = 2f

    var initialHeading: Double = 0.0

    var heading = Angle()

    var velocity = Polar2f(heading, initialSpeed)

    var turningSpeed = Angle.degrees(3.0)

    @CostumeAttribute
    var value: Int = 1

    override fun activated() {

        heading.degrees = initialHeading

        val growShrink = (Grow(actor, 1f, 2f).then(Grow(actor, 1f, 1f)).forever())
        val circle = Circle(heading, turningSpeed).and(MovePolar(actor.position, velocity))
        action = growShrink.and(circle)

        if (value < 10) {
            actor.color = Color(0.6f, 0.6f, 0.4f, 1f)

        } else if (value >= 10 && value < 100) {
            actor.color = Color(0.7f, 0.7f, 1f, 1f)
        }
    }

}
