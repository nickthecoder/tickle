package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.stage.StageView

class FollowMouse(
        val position: Vector2d,
        val view: StageView)

    : Action {

    private val mouse = Vector2d()

    override fun act(): Boolean {
        view.mousePosition(mouse)
        position.set(mouse)
        return false
    }

}
