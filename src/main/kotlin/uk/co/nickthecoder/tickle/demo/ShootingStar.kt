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

    var sideLength: Double = 400.0

    var sideDuration: Double = 1.0

    val heading = Angle.degrees(initialHeading)

    override fun activated() {

        val growShrink = (Grow(actor, 1.0, 2.0).then(Grow(actor, 1.0, 1.0)).forever())

        val edge = (Forwards(actor.position, sideLength, heading, sideDuration, Eases.easeInOutExpo)
                .and(Turn(heading, curveAngle, sideDuration, Eases.easeInOutExpo)))

                .then(Turn(heading, turnAngle, 0.0))

        action = growShrink.and(edge.forever())

    }

}
