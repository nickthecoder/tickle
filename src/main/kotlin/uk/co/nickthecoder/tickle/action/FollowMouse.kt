package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.stage.StageView

class FollowMouse(val view: StageView)

    : Action<Actor> {

    override fun act(target: Actor): Boolean {
        val mp = view.mousePosition()
        target.x = mp.x
        target.y = mp.y
        return false
    }

}
