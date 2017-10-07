package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.util.Angle

class Turn(
        val heading: Angle,
        val angle: Angle,
        seconds: Float,
        ease: Ease = Eases.linear)

    : AnimationAction(seconds, ease) {

    private var initialRadians: Double = 0.0

    private var finalRadians: Double = 0.0

    override fun storeInitialValue() {
        initialRadians = heading.radians
        finalRadians = initialRadians + angle.radians
    }

    override fun update(t: Float) {
        heading.radians = lerp(initialRadians, finalRadians, t)
    }
}
