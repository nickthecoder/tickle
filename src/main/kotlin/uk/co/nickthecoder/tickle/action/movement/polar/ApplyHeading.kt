package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Heading

class ApplyHeading(
        val heading: Heading)

    : Action<Actor> {

    override fun act(target: Actor): Boolean {
        target.directionRadians = heading.radians
        return false
    }

}
