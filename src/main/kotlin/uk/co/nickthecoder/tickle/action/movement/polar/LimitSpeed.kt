package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2f

open class LimitSpeed(
        val velocity: Polar2f,
        var maxSpeed: Float = 10f,
        var minSpeed: Float = 0f)

    : Action {

    override fun act(): Boolean {
        if (velocity.magnitude < minSpeed) {
            velocity.magnitude = minSpeed
        }
        if (velocity.magnitude > maxSpeed) {
            velocity.magnitude = maxSpeed
        }
        return false
    }

}
