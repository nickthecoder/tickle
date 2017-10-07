package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.movement.FollowMouse

class Hand : ActionRole() {

    override fun activated() {
        actor.stage?.firstView()?.let { view ->
            action = FollowMouse(view)
        }
        super.activated()
    }

}
