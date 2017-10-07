package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.events.Input
import uk.co.nickthecoder.tickle.util.Scalar

open class AcceleratePolarInput(
        val speed: Scalar,
        var acceleration: Float,
        var deceleration: Float = -acceleration,
        var autoSlow: Float = 0f,
        accelerate: String = "up",
        decelerate: String = "down")

    : Action<Actor> {

    val accelerate = Resources.instance.optionalInput(accelerate) ?: Input.dummyInput
    val decelerate = Resources.instance.optionalInput(decelerate) ?: Input.dummyInput

    override fun act(target: Actor): Boolean {
        if (accelerate.isPressed()) {
            speed.value += acceleration
        } else if (decelerate.isPressed()) {
            speed.value += deceleration
        } else {
            // Automatically slow down (gradually), when no keys are pressed
            speed.value -= autoSlow
        }
        return false
    }
}
