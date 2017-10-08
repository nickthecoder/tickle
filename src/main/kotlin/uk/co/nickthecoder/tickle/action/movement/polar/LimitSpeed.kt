package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Polar2d

open class LimitSpeed(
        val velocity: Polar2d,
        var maxSpeed: Double = 10.0,
        var minSpeed: Double = 0.0)

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
