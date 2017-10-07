package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Heading

class ApplyHeading(
        val actor : Actor,
        val heading: Heading)

    : Action {

    override fun act(): Boolean {
        actor.directionRadians = heading.radians
        return false
    }

}
