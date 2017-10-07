package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle

class ApplyHeading(
        val actor : Actor,
        val heading: Angle)

    : Action {

    override fun act(): Boolean {
        actor.direction.radians = heading.radians
        return false
    }

}
