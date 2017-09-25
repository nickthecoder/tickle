package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

class Vector4(var x: Float, var y: Float, var z: Float, var w: Float) {

    fun lengthSquared(): Float {
        return x * x + y * y + z * z + w * w
    }

    fun length(): Float {
        return Math.sqrt(lengthSquared().toDouble()).toFloat()
    }

    fun normalize(): Vector4 = this / length()

    operator fun plus(other: Vector4) = Vector4(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun unaryMinus(): Vector4 = this * -1f

    operator fun minus(other: Vector4) = this + (other.unaryMinus())


    operator fun times(scale: Float) = Vector4(x * scale, y * scale, z * scale, w * scale)

    operator fun div(divisor: Float) = this * (1 / divisor)

    fun linearInterpolation(other: Vector4, alpha: Float) = this * (1f - alpha) + (other * alpha)


    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(x).put(y).put(z).put(w)
        buffer.flip()
    }

}
