package uk.co.nickthecoder.tickle.action.animation

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.util.Angle

class Forwards(
        val position: Vector2d,
        val by: Double,
        val heading: Angle,
        seconds: Double,
        ease: Ease = Eases.linear)

    : AnimationAction(seconds, ease) {

    override fun storeInitialValue() {
    }

    override fun update(t: Double) {
        position.add(heading.vector().mul(by * delta(t)))
    }
}
