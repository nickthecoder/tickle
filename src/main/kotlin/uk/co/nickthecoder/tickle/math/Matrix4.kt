package uk.co.nickthecoder.tickle.math

import java.nio.FloatBuffer

class Matrix4(
        val m00: Float = 1f, val m10: Float = 0f, val m20: Float = 0f, val m30: Float = 0f,
        val m01: Float = 0f, val m11: Float = 1f, val m21: Float = 0f, val m31: Float = 0f,
        val m02: Float = 0f, val m12: Float = 0f, val m22: Float = 1f, val m32: Float = 0f,
        val m03: Float = 0f, val m13: Float = 0f, val m23: Float = 0f, val m33: Float = 1f) {

    constructor(col1: Vector4, col2: Vector4, col3: Vector4, col4: Vector4) : this(
            col1.x, col2.x, col3.x, col4.x,
            col1.y, col2.y, col3.y, col4.y,
            col1.z, col2.z, col3.z, col4.z,
            col1.w, col2.w, col3.w, col4.w)


    operator fun plus(other: Matrix4) = Matrix4(
            m00 + other.m00, m10 + other.m10, m20 + other.m20, m30 + other.m30,
            m01 + other.m01, m11 + other.m11, m21 + other.m21, m31 + other.m31,
            m02 + other.m02, m12 + other.m12, m22 + other.m22, m32 + other.m32,
            m03 + other.m03, m13 + other.m13, m23 + other.m23, m33 + other.m33)

    operator fun unaryMinus(): Matrix4 {
        return this * -1f
    }

    operator fun minus(other: Matrix4) = this + (-other)

    operator fun times(scale: Float) = Matrix4(
            m00 * scale, m10 * scale, m20 * scale, m30 * scale,
            m01 * scale, m11 * scale, m21 * scale, m31 * scale,
            m02 * scale, m12 * scale, m22 * scale, m32 * scale,
            m03 * scale, m13 * scale, m23 * scale, m33 * scale)

    operator fun times(other: Matrix4) = Matrix4(
            m00 * other.m00 + m10 * other.m01 + m20 * other.m02 + m30 * other.m03,
            m00 * other.m10 + m10 * other.m11 + m20 * other.m12 + m30 * other.m13,
            m00 * other.m20 + m10 * other.m21 + m20 * other.m22 + m30 * other.m23,
            m00 * other.m30 + m10 * other.m31 + m20 * other.m32 + m30 * other.m33,

            m01 * other.m00 + m11 * other.m01 + m21 * other.m02 + m31 * other.m03,
            m01 * other.m10 + m11 * other.m11 + m21 * other.m12 + m31 * other.m13,
            m01 * other.m20 + m11 * other.m21 + m21 * other.m22 + m31 * other.m23,
            m01 * other.m30 + m11 * other.m31 + m21 * other.m32 + m31 * other.m33,

            m02 * other.m00 + m12 * other.m01 + m22 * other.m02 + m32 * other.m03,
            m02 * other.m10 + m12 * other.m11 + m22 * other.m12 + m32 * other.m13,
            m02 * other.m20 + m12 * other.m21 + m22 * other.m22 + m32 * other.m23,
            m02 * other.m30 + m12 * other.m31 + m22 * other.m32 + m32 * other.m33,

            m03 * other.m00 + m13 * other.m01 + m23 * other.m02 + m33 * other.m03,
            m03 * other.m10 + m13 * other.m11 + m23 * other.m12 + m33 * other.m13,
            m03 * other.m20 + m13 * other.m21 + m23 * other.m22 + m33 * other.m23,
            m03 * other.m30 + m13 * other.m31 + m23 * other.m32 + m33 * other.m33)

    fun transpose() = Matrix4(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33)

    fun intoBuffer(buffer: FloatBuffer) {
        buffer
                .put(m00).put(m01).put(m02).put(m03)
                .put(m10).put(m11).put(m12).put(m13)
                .put(m20).put(m21).put(m22).put(m23)
                .put(m30).put(m31).put(m32).put(m33)
                .flip()
    }

    fun toRowMajorList() =
            listOf(
                    listOf(m00, m10, m20, m30),
                    listOf(m01, m11, m21, m31),
                    listOf(m02, m12, m22, m32),
                    listOf(m03, m13, m23, m33))

    override fun equals(other: Any?): Boolean {
        if (other is Matrix4) {
            return m00 == other.m00 &&
                    m10 == other.m10 &&
                    m20 == other.m20 &&
                    m30 == other.m30 &&

                    m01 == other.m01 &&
                    m11 == other.m11 &&
                    m21 == other.m21 &&
                    m31 == other.m31 &&

                    m02 == other.m02 &&
                    m12 == other.m12 &&
                    m22 == other.m22 &&
                    m32 == other.m32 &&

                    m03 == other.m03 &&
                    m13 == other.m13 &&
                    m23 == other.m23 &&
                    m33 == other.m33
        } else {
            return false
        }
    }

    override fun toString() = toRowMajorList().toString()

    companion object {

        fun orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) = Matrix4(
                // Scale
                m00 = 2f / (right - left),
                m11 = 2f / (top - bottom),
                m22 = -2f / (far - near),
                // Translation
                m30 = -(right + left) / (right - left),
                m31 = -(top + bottom) / (top - bottom),
                m32 = -(far + near) / (far - near))


        fun zRotation(radians: Double): Matrix4 {
            val sin = Math.sin(radians).toFloat()
            val cos = Math.cos(radians).toFloat()

            return Matrix4(
                    cos, -sin, 0f, 0f,
                    sin, cos, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f)

        }

        fun translate(x: Float, y: Float, z: Float): Matrix4 {
            return Matrix4(
                    1f, 0f, 0f, x,
                    0f, 1f, 0f, y,
                    0f, 0f, 1f, z,
                    0f, 0f, 0f, 1f)
        }

        fun zRotation2(x: Float, y: Float, radians: Double) = translate(x, y, 0f) * zRotation(radians) * translate(-x, -y, 0f)

        fun zRotation(x: Float, y: Float, radians: Double): Matrix4 {
            val sin = Math.sin(radians).toFloat()
            val cos = Math.cos(radians).toFloat()

            return Matrix4(
                    cos, -sin, 0f, -cos * x + x + sin * y,
                    sin, cos, 0f, -sin * x - cos * y + y,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f)
        }
    }

}

