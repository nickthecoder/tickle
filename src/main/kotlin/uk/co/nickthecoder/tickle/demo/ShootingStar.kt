package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.action.animation.Turn
import uk.co.nickthecoder.tickle.util.Angle

class ShootingStar() : ActionRole() {

    val curveAngle = Angle.degrees(60.0)

    val turnAngle = Angle.degrees(190.0)

    var initialHeading: Double = 0.0

    var sideLength: Float = 400f

    var sideDuration: Float = 1f

    val heading = Angle.degrees(initialHeading)

    override fun activated() {

        val growShrink = (Grow(actor, 1f, 2f).then(Grow(actor, 1f, 1f)).forever())

        val edge = (Forwards(actor.position, sideLength, heading, sideDuration, Eases.easeInOutExpo)
                .and(Turn(heading, curveAngle, sideDuration, Eases.easeInOutExpo)))

                .then(Turn(heading, turnAngle, 0f))

        action = growShrink.and(edge.forever())

    }

}
