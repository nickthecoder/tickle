package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp

open class MoveTo(
        val position: Vector2d,
        seconds: Double,
        val finalPosition: Vector2d,
        ease: Ease = LinearEase.instance)

    : AnimationAction(seconds, ease) {

    private var initialPosition = Vector2d()

    override fun storeInitialValue() {
        initialPosition.set(position)
    }

    override fun update(t: Double) {
        position.lerp(initialPosition, finalPosition, t)
    }
}
