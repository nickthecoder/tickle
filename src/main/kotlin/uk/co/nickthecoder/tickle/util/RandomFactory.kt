package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector4d
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.graphics.Color
import java.util.*

class RandomFactory(seed: Long? = null) {

    val random = if (seed == null) Random() else Random(seed)

    fun nextDouble(ease: Ease = LinearEase.instance) = ease.ease(random.nextDouble())

    fun plusMinus(limit: Double, ease: Ease = LinearEase.instance) = nextDouble(ease) * limit * 2 - limit

    fun plusMinus(limit: Float, ease: Ease = LinearEase.instance): Float = (nextDouble(ease) * limit * 2 - limit).toFloat()

    fun between(from: Color, to: Color, ease: Ease = LinearEase.instance) = from.lerp(to, nextDouble(ease).toFloat())

    fun between(from: Polar2d, to: Polar2d, ease: Ease = LinearEase.instance) = Polar2d(from).lerp(to, nextDouble(ease))

    fun between(from: Vector2d, to: Vector2d, ease: Ease = LinearEase.instance) = Vector2d(from).lerp(to, nextDouble(ease))

    fun between(from: Vector3d, to: Vector3d, ease: Ease = LinearEase.instance) = Vector3d(from).lerp(to, nextDouble(ease))

    fun between(from: Vector4d, to: Vector4d, ease: Ease = LinearEase.instance) = Vector4d(from).lerp(to, nextDouble(ease))

    companion object {

        /**
         * A shared instance. This is a deliberately short name, looks like "Rand.om" when combined with the
         * class name!
         */
        val instance = RandomFactory()
    }

}

/**
 * Static methods using a shared instance of RandomFactory
 */
object Rand {
    fun plusMinus(limit: Double, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)
    fun plusMinus(limit: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)

    fun between(from: Color, to: Color, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Polar2d, to: Polar2d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Vector2d, to: Vector2d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Vector3d, to: Vector3d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Vector4d, to: Vector4d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
}
