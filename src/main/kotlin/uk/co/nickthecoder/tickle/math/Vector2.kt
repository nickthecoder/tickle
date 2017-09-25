package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

/**
 * This class represents a (x,y,z)-Vector. GLSL equivalent to vec3.
 */
class Vector2(var x: Float, var y: Float) {

    fun lengthSquared(): Float {
        return x * x + y * y
    }

    fun length(): Float {
        return Math.sqrt(lengthSquared().toDouble()).toFloat()
    }

    fun normalize() = this / length()

    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)

    operator fun unaryMinus() = this * -1f

    operator fun minus(other: Vector2) = this + (other.unaryMinus())

    operator fun times(scale: Float) = Vector2(x * scale, y * scale)

    operator fun div(divisor: Float) = this * (1 / divisor)

    fun linearInterpolation(other: Vector2, alpha: Float): Vector2 {
        return this * (1f - alpha) + (other * alpha)
    }

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(x).put(y)
        buffer.flip()
    }

}
