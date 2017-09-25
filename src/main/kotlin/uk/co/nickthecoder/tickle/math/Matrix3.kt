package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

class Matrix3(
        val m00: Float = 1f, val m10: Float = 0f, val m20: Float = 0f,
        val m01: Float = 0f, val m11: Float = 1f, val m21: Float = 0f,
        val m02: Float = 0f, val m12: Float = 0f, val m22: Float = 1f) {

    constructor(col1: Vector3, col2: Vector3, col3: Vector3) : this(
            col1.x, col2.x, col3.x,
            col1.y, col2.y, col3.y,
            col1.z, col2.z, col3.z)


    operator fun plus(other: Matrix3) = Matrix3(
            m00 + other.m00, m10 + other.m10, m20 + other.m20,
            m01 + other.m01, m11 + other.m11, m21 + other.m21,
            m02 + other.m02, m12 + other.m12, m22 + other.m22)

    operator fun unaryMinus(): Matrix3 {
        return this * -1f
    }

    operator fun minus(other: Matrix3) = this + (-other)

    operator fun times(scale: Float) = Matrix3(
            m00 * scale, m10 * scale, m20 * scale,
            m01 * scale, m11 * scale, m21 * scale,
            m02 * scale, m12 * scale, m22 * scale)

    operator fun times(vector: Vector3) = Vector3(
            m00 * vector.x + m01 * vector.y + m02 * vector.z,
            m10 * vector.x + m11 * vector.y + m12 * vector.z,
            m20 * vector.x + m21 * vector.y + m22 * vector.z)

    fun multiply(other: Matrix3) = Matrix3(
            m00 * other.m00 + m01 * other.m10 + m02 * other.m20,
            m10 * other.m00 + m11 * other.m10 + m12 * other.m20,
            m20 * other.m00 + m21 * other.m10 + m22 * other.m20,

            m00 * other.m01 + m01 * other.m11 + m02 * other.m21,
            m10 * other.m01 + m11 * other.m11 + m12 * other.m21,
            m20 * other.m01 + m21 * other.m11 + m22 * other.m21,

            m00 * other.m02 + m01 * other.m12 + m02 * other.m22,
            m10 * other.m02 + m11 * other.m12 + m12 * other.m22,
            m20 * other.m02 + m21 * other.m12 + m22 * other.m22)

    fun transpose() = Matrix3(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22)

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(m00).put(m10).put(m20)
        buffer.put(m01).put(m11).put(m21)
        buffer.put(m02).put(m12).put(m22)
        buffer.flip()
    }

    fun toList() =
            listOf(
                    listOf(m00, m10, m20),
                    listOf(m01, m11, m21),
                    listOf(m02, m12, m22))

    override fun toString() = toList().toString()

}
