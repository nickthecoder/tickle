package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor

class Grow(
        val actor: Actor,
        seconds: Double,
        val finalScale: Double,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialScale: Double = 0.0

    override fun storeInitialValue() {
        initialScale = actor.scale
    }


    override fun update(t: Double) {
        actor.scale = lerp(initialScale, finalScale, t)
    }

}
