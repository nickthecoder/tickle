package uk.co.nickthecoder.tickle

import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.graphics.Color
import java.util.*

class RandomFactory(seed: Long? = null) {

    val random = if (seed == null) Random() else Random(seed)

    fun nextFloat(ease: Ease = LinearEase.instance) = ease.ease(random.nextFloat())

    fun plusMinus(limit: Float, ease: Ease = LinearEase.instance) = nextFloat(ease) * limit * 2 - limit

    fun between(from: Float, to: Float, ease: Ease = LinearEase.instance) = nextFloat(ease) * (to - from) + from

    fun between(from: Color, to: Color, ease: Ease = LinearEase.instance) = from.linearInterpolation(to, nextFloat(ease))

    fun between(from: Vector2f, to: Vector2f, ease: Ease = LinearEase.instance) = Vector2f(from).lerp(to, nextFloat(ease))

    fun between(from: Vector3f, to: Vector3f, ease: Ease = LinearEase.instance) = Vector3f(from).lerp(to, nextFloat(ease))

    fun between(from: Vector4f, to: Vector4f, ease: Ease = LinearEase.instance) = Vector4f(from).lerp(to, nextFloat(ease))

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
    fun plusMinus(limit: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)

    fun between(from: Float, to: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to)
    fun between(from: Color, to: Color, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to)
    fun between(from: Vector2f, to: Vector2f, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to)
    fun between(from: Vector3f, to: Vector3f, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to)
    fun between(from: Vector4f, to: Vector4f, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to)
}
