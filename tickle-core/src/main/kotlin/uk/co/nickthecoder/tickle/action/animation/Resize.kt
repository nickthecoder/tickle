package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor

class Resize(
        val actor: Actor,
        seconds: Double,
        val finalWidth: Double = actor.appearance.width(),
        val finalHeight: Double = actor.appearance.height(),
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialWidth = 0.0
    private var initialHeight = 0.0

    override fun storeInitialValue() {
        initialWidth = actor.appearance.width()
        initialHeight = actor.appearance.height()
    }

    override fun update(t: Double) {
        actor.resize(lerp(initialWidth, finalWidth, t), lerp(initialHeight, finalHeight, t))
    }

}
