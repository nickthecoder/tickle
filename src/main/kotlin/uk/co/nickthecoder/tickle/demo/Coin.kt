package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.HeadingMovement
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Grow
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class Coin() : ActionRole() {

    @Attribute
    var speed: Float = 1f

    @Attribute(AttributeType.DIRECTION)
    var heading: Double = 0.0

    @Attribute
    var rotationSpeed: Double = 3.0

    @CostumeAttribute
    var value: Int = 1

    override fun activated() {

        action = HeadingMovement(speed, heading, speedDegrees = rotationSpeed)
                .and(
                        Grow(1f, 1.3f, Eases.easeInOut)
                                .then(Grow(0.5f, 1f, Eases.easeInOut)

                                ).forever()
                )

        if (value < 10) {
            actor.color = Color(0.6f, 0.6f, 0.4f, 1f)

        } else if (value >= 10 && value < 100) {
            actor.color = Color(0.7f, 0.7f, 1f, 1f)
        }
    }

}
