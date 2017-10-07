package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Scalar

open class LimitSpeed(
        val speed: Scalar,
        var maxSpeed: Float = 10f,
        var minSpeed: Float = 0f)

    : Action<Actor> {

    override fun act(target: Actor): Boolean {
        if (speed.value < minSpeed) {
            speed.value = minSpeed
        }
        if (speed.value > maxSpeed) {
            speed.value = maxSpeed
        }
        return false
    }

}
