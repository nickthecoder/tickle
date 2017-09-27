package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

/**
 * Represents a (x,y) Vector
 */
class Vector2(var x: Float, var y: Float) {

    fun lengthSquared(): Float {
        return x * x + y * y
    }

    fun length(): Float {
        return Math.sqrt(lengthSquared().toDouble()).toFloat()
    }

    fun normalize(): Vector2 = this / length()

    operator fun unaryMinus(): Vector2 = this * -1f

    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)

    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)

    operator fun times(scale: Float) = Vector2(x * scale, y * scale)

    operator fun div(divisor: Float) = Vector2(x / divisor, y / divisor)

    fun linearInterpolation(other: Vector2, alpha: Float) = this * (1f - alpha) + (other * alpha)

    fun dotProduct(other: Vector3): Float {
        return x * other.x + y * other.y
    }

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(x).put(y)
        buffer.flip()
    }

}
