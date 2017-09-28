package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor

class Grow(
        seconds: Float,
        val finalScale: Float,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialScale: Float = 0f

    override fun storeInitialValue(actor: Actor) {
        initialScale = actor.scale
    }


    override fun update(actor: Actor, t: Float) {
        actor.scale = initialScale * (1 - t) + finalScale * t
    }

}
