package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

/**
 * Represents a (x,y,z,w) Vector
 */
class Vector4(var x: Float, var y: Float, var z: Float, var w: Float) {

    fun lengthSquared(): Float {
        return x * x + y * y + z * z + w * w
    }

    fun length(): Float {
        return Math.sqrt(lengthSquared().toDouble()).toFloat()
    }

    fun normalize(): Vector4 = this / length()

    operator fun unaryMinus(): Vector4 = this * -1f

    operator fun plus(other: Vector4) = Vector4(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun minus(other: Vector4) = Vector4(x - other.x, y - other.y, z - other.z, w - other.w)

    operator fun times(scale: Float) = Vector4(x * scale, y * scale, z * scale, w * scale)

    operator fun div(divisor: Float) = Vector4(x / divisor, y / divisor, z / divisor, w / divisor)

    fun linearInterpolation(other: Vector4, alpha: Float) = this * (1f - alpha) + (other * alpha)
    
    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(x).put(y).put(z).put(w)
        buffer.flip()
    }

}
