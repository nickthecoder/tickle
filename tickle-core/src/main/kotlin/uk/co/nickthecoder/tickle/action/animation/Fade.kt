package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.graphics.Color

class Fade(
        val color: Color,
        seconds: Double,
        val finalAlpha: Float,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialAlpha = 0f

    override fun storeInitialValue() {
        initialAlpha = color.alpha
    }

    override fun update(t: Double) {
        color.alpha = lerp(initialAlpha, finalAlpha, t.toFloat())
    }
}
