package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.graphics.Color

class Fade(
        seconds: Float,
        val finalColor: Color,
        ease: Ease = LinearEase.instance)

    : AnimationAction<Actor>(seconds, ease) {

    private lateinit var initialColor: Color

    override fun storeInitialValue(target: Actor) {
        initialColor = target.color
    }

    override fun update(target: Actor, t: Float) {
        target.color = initialColor.linearInterpolation(finalColor, t)
    }
}
