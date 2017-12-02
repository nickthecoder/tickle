package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor

class Resize(
        val actor: Actor,
        seconds: Double,
        val finalWidth: Double,
        val finalHeight: Double,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialWidth = 0.0
    private var initialHeight = 0.0

    override fun storeInitialValue() {
        initialWidth = actor.appearance.width()
        initialHeight = actor.appearance.width()
    }

    override fun update(t: Double) {
        actor.resize(lerp(initialWidth, finalWidth, t), lerp(initialHeight, finalHeight, t))
    }

}
