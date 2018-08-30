package uk.co.nickthecoder.tickle.action

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.stage.StageView

class CenterView(
        val stageView: StageView,
        val position: Vector2d)

    : Action {

    override fun act(): Boolean {
        stageView.centerX = position.x
        stageView.centerY = position.y

        return false
    }
}