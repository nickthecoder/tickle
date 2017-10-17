package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.graphics.Color

class Colorize(
        val color: Color,
        seconds: Double,
        val finalColor: Color,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private val initialColor = Color.white()

    override fun storeInitialValue() {
        initialColor.set(color)
    }

    override fun update(t: Double) {
        initialColor.lerp(finalColor, t.toFloat(), color)
    }
}
