package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Angle

class ChangeDirection(
        val actor : Actor,
        val direction: Angle)

    : Action {

    override fun act(): Boolean {
        actor.direction.radians = direction.radians
        return false
    }

}
