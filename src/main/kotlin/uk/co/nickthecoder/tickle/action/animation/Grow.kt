package uk.co.nickthecoder.tickle.action.animation

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor

class Grow(
        val actor: Actor,
        seconds: Double,
        val finalScale: Vector2d,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    constructor(actor: Actor, seconds: Double, finalScale: Double, ease: Ease = LinearEase.instance)
            : this(actor, seconds = seconds, finalScale = Vector2d(finalScale, finalScale), ease = ease)

    private var initialScale = Vector2d()

    override fun storeInitialValue() {
        initialScale.set(actor.scale)
    }


    override fun update(t: Double) {
        lerp(initialScale, finalScale, t, actor.scale)
    }

}
