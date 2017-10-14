package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.movement.FollowMouse

class Hand : ActionRole() {

    override fun createAction(): Action? = FollowMouse(actor.position, actor.stage!!.firstView()!!)

}
