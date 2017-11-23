package uk.co.nickthecoder.tickle.action

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.stage.StageView

class CenterViewBetween(
        val stageView: StageView,
        val positionA: Vector2d,
        val positionB: Vector2d,
        seconds: Double = 0.5,
        ease: Ease = Eases.easeInOut)

    : AnimationAction(seconds, ease) {

    override fun storeInitialValue() {
    }

    override fun update(t: Double) {
        stageView.centerX = lerp(positionA.x, positionB.x, t)
        stageView.centerY = lerp(positionA.y, positionB.y, t)
    }
}