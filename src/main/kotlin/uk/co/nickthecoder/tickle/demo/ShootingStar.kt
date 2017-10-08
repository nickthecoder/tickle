package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Forwards
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.action.animation.Turn
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Polar2d

class ShootingStar() : ActionRole() {

    //@Attribute(AttributeType.DIRECTION)
    //var heading = Angle.degrees(0.0)

    //@Attribute
    //var sideLength: Double = 400.0

    @Attribute(AttributeType.POLAR)
    var headingAndLength = Polar2d(Angle.degrees(15.0), 400.0)

    @Attribute
    var curveAngle = Angle.degrees(60.0)

    @Attribute
    var turnAngle = Angle.degrees(190.0)

    @Attribute
    var sideDuration: Double = 1.0

    override fun activated() {

        val growShrink = (Grow(actor, 1.0, 2.0).then(Grow(actor, 1.0, 1.0)).forever())

        val edge = (Forwards(actor.position, headingAndLength.magnitude, headingAndLength.angle, sideDuration, Eases.easeInOutExpo)
                .and(Turn(headingAndLength.angle, curveAngle, sideDuration, Eases.easeInOutExpo)))

                .then(Turn(headingAndLength.angle, turnAngle, 0.0))

        //val edge = (Forwards(actor.position, sideLength, heading, sideDuration, Eases.easeInOutExpo)
        //        .and(Turn(heading, curveAngle, sideDuration, Eases.easeInOutExpo)))
        //        .then(Turn(heading, turnAngle, 0.0))

        action = growShrink.and(edge.forever())

    }

}
