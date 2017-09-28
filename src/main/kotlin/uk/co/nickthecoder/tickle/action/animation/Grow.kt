package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor

class Grow(
        seconds: Float,
        val finalScale: Float,
        ease: Ease = LinearEase.instance)

    : AnimationAction<Actor>(seconds, ease) {

    private var initialScale: Float = 0f

    override fun storeInitialValue(target: Actor) {
        initialScale = target.scale
    }


    override fun update(target: Actor, t: Float) {
        target.scale = initialScale * (1 - t) + finalScale * t
    }

}
