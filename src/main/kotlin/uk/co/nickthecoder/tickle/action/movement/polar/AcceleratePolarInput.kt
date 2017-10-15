package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.util.Polar2d

open class AcceleratePolarInput(
        val velocatiry: Polar2d,
        var acceleration: Double,
        var deceleration: Double = -acceleration,
        var autoSlow: Double = 0.0,
        accelerate: String = "up",
        decelerate: String = "down")

    : Action {

    val accelerate = Resources.instance.optionalInput(accelerate) ?: Input.dummyInput
    val decelerate = Resources.instance.optionalInput(decelerate) ?: Input.dummyInput

    override fun act(): Boolean {
        if (accelerate.isPressed()) {
            velocatiry.magnitude += acceleration
        } else if (decelerate.isPressed()) {
            velocatiry.magnitude += deceleration
        } else {
            // Automatically slow down (gradually), when no keys are pressed
            velocatiry.magnitude -= autoSlow
        }
        return false
    }
}
