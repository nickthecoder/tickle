package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor

class Grow(
        val actor: Actor,
        seconds: Float,
        val finalScale: Float,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialScale: Float = 0f

    override fun storeInitialValue() {
        initialScale = actor.scale
    }


    override fun update(t: Float) {
        actor.scale = lerp(initialScale, finalScale, t)
    }

}
