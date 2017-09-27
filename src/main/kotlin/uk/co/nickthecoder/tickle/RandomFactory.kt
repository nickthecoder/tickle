package uk.co.nickthecoder.tickle

import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import uk.co.nickthecoder.tickle.graphics.Color
import java.util.*

class RandomFactory(seed: Long? = null) {

    val random = if (seed == null) Random() else Random(seed)


    fun plusMinus(limit: Float) = random.nextFloat() * limit * 2 - limit

    fun between(from: Float, to: Float) = random.nextFloat() * (to - from) + from

    fun between(from: Color, to: Color) = from.linearInterpolation(to, random.nextFloat())

    fun between(from: Vector2f, to: Vector2f) = Vector2f(from).lerp(to, random.nextFloat())

    fun between(from: Vector3f, to: Vector3f) = Vector3f(from).lerp(to, random.nextFloat())

    fun between(from: Vector4f, to: Vector4f) = Vector4f(from).lerp(to, random.nextFloat())

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
    fun plusMinus(limit: Float) = RandomFactory.instance.plusMinus(limit)

    fun between(from: Float, to: Float) = RandomFactory.instance.between(from, to)
    fun between(from: Color, to: Color) = RandomFactory.instance.between(from, to)
    fun between(from: Vector2f, to: Vector2f) = RandomFactory.instance.between(from, to)
    fun between(from: Vector3f, to: Vector3f) = RandomFactory.instance.between(from, to)
    fun between(from: Vector4f, to: Vector4f) = RandomFactory.instance.between(from, to)
}
