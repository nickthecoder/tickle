package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.action.HeadingMovement

class Rotating(val speed: Float, val heading: Double, val rotationSpeed: Double) : AbstractRole() {

    // TODO Add parameters for the speed, heading etc.

    override fun activated() {
        actions.add(HeadingMovement(speed, heading, speedDegrees = rotationSpeed))
    }

}
