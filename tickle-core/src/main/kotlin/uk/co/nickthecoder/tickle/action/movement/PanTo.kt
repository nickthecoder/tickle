package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.stage.StageView

open class PanTo(
        val view: StageView,
        val finalPosition: Vector2d,
        seconds: Double,
        ease: Ease = LinearEase())

    : AnimationAction(seconds, ease) {

    var initialPosition = Vector2d()

    override fun storeInitialValue() {
        initialPosition.set(view.centerX, view.centerY)
    }

    override fun update(t: Double) {
        view.centerX = lerp(initialPosition.x, finalPosition.x, t)
        view.centerY = lerp(initialPosition.y, finalPosition.y, t)
    }

}
