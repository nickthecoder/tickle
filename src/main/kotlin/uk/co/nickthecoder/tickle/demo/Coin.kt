package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.Grow
import uk.co.nickthecoder.tickle.action.HeadingMovement

class Coin(val speed: Float, val heading: Double, val rotationSpeed: Double) : AbstractRole() {

    // TODO Add parameters for the speed, heading etc.

    val action = HeadingMovement(speed, heading, speedDegrees = rotationSpeed)
            .and(
                    Grow(2f, 1f).then(Grow(1f, 0.2f)).forever()
            )

    override fun activated() {
        action.begin(actor)
    }

    override fun tick() {
        action.act(actor)
    }
}
