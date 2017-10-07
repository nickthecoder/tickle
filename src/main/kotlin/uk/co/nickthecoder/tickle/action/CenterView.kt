package uk.co.nickthecoder.tickle.action

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.stage.StageView

class CenterView(
        val stageView: StageView,
        val position: Vector2f)

    : Action {

    override fun act(): Boolean {
        stageView.centerX = position.x
        stageView.centerY = position.y

        return false
    }
}