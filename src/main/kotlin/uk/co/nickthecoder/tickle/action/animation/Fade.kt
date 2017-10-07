package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.graphics.Color

class Fade(
        val actor: Actor,
        seconds: Float,
        val finalColor: Color,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private lateinit var initialColor: Color

    override fun storeInitialValue() {
        initialColor = actor.color
    }

    override fun update(t: Float) {
        actor.color = initialColor.linearInterpolation(finalColor, t)
    }
}
