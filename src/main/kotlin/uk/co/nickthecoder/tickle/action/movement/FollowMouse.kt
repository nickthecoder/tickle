package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import org.joml.Vector2f
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.stage.StageView

class FollowMouse(
        val position: Vector2d,
        val view: StageView)

    : Action {

    override fun act(): Boolean {
        val mp = view.mousePosition()
        position.x = mp.x
        position.y = mp.y
        return false
    }

}
