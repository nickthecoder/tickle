package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

class Matrix2(
        val m00: Float = 1f, val m10: Float = 0f,
        val m01: Float = 0f, val m11: Float = 1f) {

    constructor(col1: Vector2, col2: Vector2) : this(
            col1.x, col2.x,
            col1.y, col2.y)


    operator fun plus(other: Matrix2) = Matrix3(
            m00 + other.m00, m10 + other.m10,
            m01 + other.m01, m11 + other.m11)

    operator fun unaryMinus(): Matrix2 {
        return this * -1f
    }

    operator fun minus(other: Matrix2) = this + (-other)

    operator fun times(scale: Float) = Matrix2(
            m00 * scale, m10 * scale,
            m01 * scale, m11 * scale)

    operator fun times(vector: Vector2) = Vector2(
            m00 * vector.x + m01 * vector.y,
            m10 * vector.x + m11 * vector.y)

    fun multiply(other: Matrix2) = Matrix2(
            m00 * other.m00 + m01 * other.m10,
            m10 * other.m00 + m11 * other.m10,

            m00 * other.m01 + m01 * other.m11,
            m10 * other.m01 + m11 * other.m11)

    fun transpose() = Matrix3(
            m00, m01,
            m10, m11)

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(m00).put(m10)
        buffer.put(m01).put(m11)
        buffer.flip()
    }

    fun toList() =
            listOf(
                    listOf(m00, m10),
                    listOf(m01, m11))

    override fun toString() = toList().toString()

}
