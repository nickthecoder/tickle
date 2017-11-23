package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp

open class MoveBy(
        val position: Vector2d,
        val amount: Vector2d,
        seconds: Double,
        ease: Ease = LinearEase())

    : AnimationAction(seconds, ease) {

    var initialPosition = Vector2d()
    var finalPosition = Vector2d()

    override fun storeInitialValue() {
        initialPosition.set(position)
        initialPosition.add(amount, finalPosition)
    }

    override fun update(t: Double) {
        lerp(initialPosition, finalPosition, t, position)
    }

}
