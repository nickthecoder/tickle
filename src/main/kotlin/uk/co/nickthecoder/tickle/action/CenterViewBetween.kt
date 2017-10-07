package uk.co.nickthecoder.tickle.action

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.stage.StageView

class CenterViewBetween(
        val stageView: StageView,
        val positionA: Vector2f,
        val positionB: Vector2f,
        seconds: Float = 0.5f,
        ease: Ease = Eases.easeInOut)

    : AnimationAction(seconds, ease) {

    override fun storeInitialValue() {
    }

    override fun update(t: Float) {
        stageView.centerX = lerp(positionA.x, positionB.x, t)
        stageView.centerY = lerp(positionA.y, positionB.y, t)
    }
}