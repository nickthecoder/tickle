package uk.co.nickthecoder.tickle.action.movement

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.util.Angle


open class Circle(
        val position: Vector2d,
        val radius: Double,
        seconds: Double,
        val fromAngle: Double = 0.0,
        val toAngle: Double = Math.PI * 2,
        ease: Ease = LinearEase.instance,
        val radiusY: Double = radius)

    : AnimationAction(seconds, ease) {

    constructor(
            position: Vector2d,
            radius: Double,
            seconds: Double,
            fromAngle: Angle,
            toAngle: Angle,
            ease: Ease = LinearEase.instance,
            radiusY: Double = radius)

            : this(position, radius, seconds, fromAngle.radians, toAngle.radians, ease, radiusY)

    var center = Vector2d()

    override fun storeInitialValue() {
        center.set(position.x - Math.cos(fromAngle) * radius, position.y - Math.sin(fromAngle) * radiusY)
    }

    override fun update(t: Double) {
        val angle = lerp(fromAngle, toAngle, t)
        position.x = center.x + Math.cos(angle) * radius
        position.y = center.y + Math.sin(angle) * radiusY
    }

}
