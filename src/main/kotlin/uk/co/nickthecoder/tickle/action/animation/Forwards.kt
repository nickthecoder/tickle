package uk.co.nickthecoder.tickle.action.animation

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.util.Angle

class Forwards(
        val position: Vector2f,
        val by: Float,
        val heading: Angle,
        seconds: Float,
        ease: Ease = Eases.linear)

    : AnimationAction(seconds, ease) {

    override fun storeInitialValue() {
    }

    override fun update(t: Float) {
        position.add(heading.vector().mul(by * delta(t)))
    }
}
