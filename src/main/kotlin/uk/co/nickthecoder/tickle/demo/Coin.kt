package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.HeadingMovement
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Grow

class Coin(val speed: Float, val heading: Double, val rotationSpeed: Double) : AbstractRole() {

    val action = HeadingMovement(speed, heading, speedDegrees = rotationSpeed)
            .and(
                    Grow(1f, 1.3f, Eases.easeInOut)
                            .then(Grow(0.5f, 1f, Eases.easeInOut)

                            ).forever()
            )

    override fun activated() {
        action.begin(actor)
    }

    override fun tick() {
        action.act(actor)
    }
}
