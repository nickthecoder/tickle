package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.FollowMouse

class Hand : ActionRole() {

    override fun activated() {
        action = FollowMouse(Game.instance.scene.findStageView("main")!!)
        super.activated()
    }

}
