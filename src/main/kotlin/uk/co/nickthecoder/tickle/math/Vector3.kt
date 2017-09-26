package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

/**
 * Represents a (x,y,z) Vector
 */
class Vector3(var x: Float, var y: Float, var z: Float) {

    fun lengthSquared(): Float {
        return x * x + y * y + z * z
    }

    fun length(): Float {
        return Math.sqrt(lengthSquared().toDouble()).toFloat()
    }

    fun normalize(): Vector3 = this / length()

    operator fun unaryMinus(): Vector3 = this * -1f

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)

    operator fun times(scale: Float) = Vector3(x * scale, y * scale, z * scale)

    operator fun div(divisor: Float) = Vector3(x / divisor, y / divisor, z / divisor)

    fun linearInterpolation(other: Vector3, alpha: Float) = this * (1f - alpha) + (other * alpha)

    fun dotProduct(other: Vector3): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun crossProduct(other: Vector3): Vector3 {
        val x = y * other.z - z * other.y
        val y = z * other.x - x * other.z
        val z = x * other.y - y * other.x
        return Vector3(x, y, z)
    }

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(x).put(y).put(z)
        buffer.flip()
    }

}
