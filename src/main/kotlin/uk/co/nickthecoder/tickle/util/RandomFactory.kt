package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector4d
import uk.co.nickthecoder.tickle.action.animation.Ease
import uk.co.nickthecoder.tickle.action.animation.LinearEase
import uk.co.nickthecoder.tickle.action.animation.lerp
import uk.co.nickthecoder.tickle.graphics.Color
import java.util.*

class RandomFactory(seed: Long? = null) {

    val random = if (seed == null) Random() else Random(seed)

    fun nextInt(max: Int) = random.nextInt(max)

    fun nextDouble(ease: Ease = LinearEase.instance) = ease.ease(random.nextDouble())

    fun nextFloat(ease: Ease = LinearEase.instance) = ease.ease(random.nextDouble()).toFloat()

    fun plusMinus(limit: Double, ease: Ease = LinearEase.instance) = nextDouble(ease) * limit * 2 - limit

    fun plusMinus(limit: Float, ease: Ease = LinearEase.instance): Float = (nextDouble(ease) * limit * 2 - limit).toFloat()

    fun between(from: Double, to: Double, ease: Ease = LinearEase.instance) = lerp(from, to, nextDouble(ease))

    fun between(from: Float, to: Float, ease: Ease = LinearEase.instance) = lerp(from, to, nextFloat(ease))

    fun between(from: Color, to: Color, ease: Ease = LinearEase.instance): Color {
        val result = Color()
        from.lerp(to, nextDouble(ease).toFloat(), result)
        return result
    }

    fun between(from: Polar2d, to: Polar2d, ease: Ease = LinearEase.instance) = Polar2d(from).lerp(to, nextDouble(ease))

    fun between(from: Vector2d, to: Vector2d, ease: Ease = LinearEase.instance): Vector2d = Vector2d(from).lerp(to, nextDouble(ease))

    fun between(from: Vector3d, to: Vector3d, ease: Ease = LinearEase.instance): Vector3d = Vector3d(from).lerp(to, nextDouble(ease))

    fun between(from: Vector4d, to: Vector4d, ease: Ease = LinearEase.instance): Vector4d = Vector4d(from).lerp(to, nextDouble(ease))

    companion object {

        /**
         * A shared instance.
         */
        val instance = RandomFactory()
    }

}

inline fun <reified T> RandomFactory.item(list: List<T>): T {
    return list[nextInt(list.size)]
}

/**
 * Static methods using a shared instance of RandomFactory
 */
object Rand {
    inline fun <reified T> item(list: List<T>) = RandomFactory.instance.item(list)

    fun plusMinus(limit: Double, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)
    fun plusMinus(limit: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.plusMinus(limit, ease)

    fun between(from: Double, to: Double, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Float, to: Float, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Color, to: Color, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Polar2d, to: Polar2d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Vector2d, to: Vector2d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Vector3d, to: Vector3d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
    fun between(from: Vector4d, to: Vector4d, ease: Ease = LinearEase.instance) = RandomFactory.instance.between(from, to, ease)
}
