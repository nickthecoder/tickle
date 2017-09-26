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

    operator fun times(vector: Vector4) = Vector4(
            m00 * vector.x + m01 * vector.y + m02 * vector.z + m03 * vector.w,
            m10 * vector.x + m11 * vector.y + m12 * vector.z + m13 * vector.w,
            m20 * vector.x + m21 * vector.y + m22 * vector.z + m23 * vector.w,
            m30 * vector.x + m31 * vector.y + m32 * vector.z + m33 * vector.w)

    operator fun times(other: Matrix4) = Matrix4(
            m00 * other.m00 + m01 * other.m10 + m02 * other.m20 + m03 * other.m30,
            m10 * other.m00 + m11 * other.m10 + m12 * other.m20 + m13 * other.m30,
            m20 * other.m00 + m21 * other.m10 + m22 * other.m20 + m23 * other.m30,
            m30 * other.m00 + m31 * other.m10 + m32 * other.m20 + m33 * other.m30,

            m00 * other.m01 + m01 * other.m11 + m02 * other.m21 + m03 * other.m31,
            m10 * other.m01 + m11 * other.m11 + m12 * other.m21 + m13 * other.m31,
            m20 * other.m01 + m21 * other.m11 + m22 * other.m21 + m23 * other.m31,
            m30 * other.m01 + m31 * other.m11 + m32 * other.m21 + m33 * other.m31,

            m00 * other.m02 + m01 * other.m12 + m02 * other.m22 + m03 * other.m32,
            m10 * other.m02 + m11 * other.m12 + m12 * other.m22 + m13 * other.m32,
            m20 * other.m02 + m21 * other.m12 + m22 * other.m22 + m23 * other.m32,
            m30 * other.m02 + m31 * other.m12 + m32 * other.m22 + m33 * other.m32,

            m00 * other.m03 + m01 * other.m13 + m02 * other.m23 + m03 * other.m33,
            m10 * other.m03 + m11 * other.m13 + m12 * other.m23 + m13 * other.m33,
            m20 * other.m03 + m21 * other.m13 + m22 * other.m23 + m23 * other.m33,
            m30 * other.m03 + m31 * other.m13 + m32 * other.m23 + m33 * other.m33)

    fun transpose() = Matrix4(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33)

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(m00).put(m10).put(m20).put(m30)
        buffer.put(m01).put(m11).put(m21).put(m31)
        buffer.put(m02).put(m12).put(m22).put(m32)
        buffer.put(m03).put(m13).put(m23).put(m33)
        buffer.flip()
    }

    fun toList() =
            listOf(
                    listOf(m00, m10, m20, m30),
                    listOf(m01, m11, m21, m31),
                    listOf(m02, m12, m22, m32),
                    listOf(m03, m13, m23, m33))

    override fun toString() = toList().toString()

    companion object {

        fun orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) = Matrix4(
                m00 = 2f / (right - left),
                m11 = 2f / (top - bottom),
                m22 = -2f / (far - near),
                m03 = -(right + left) / (right - left),
                m13 = -(top + bottom) / (top - bottom),
                m23 = -(far + near) / (far - near))


        fun zRotation(radians: Double): Matrix4 {
            val sin = Math.sin(radians).toFloat()
            val cos = Math.cos(radians).toFloat()

            return Matrix4(
                    cos, -sin, 0f, 0f,
                    sin, cos, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f)

        }

        fun translate(x: Float, y: Float, z: Float) =
                Matrix4(
                        1f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f,
                        0f, 0f, 1f, 0f,
                        x, y, z, 1f)

        fun zRotation(x: Float, y: Float, radians: Double) = translate(x, y, 0f) * zRotation(radians) * translate(-x, -y, 0f)

        fun zRotation2(x: Float, y: Float, radians: Double): Matrix4 {
            val sin = Math.sin(radians).toFloat()
            val cos = Math.cos(radians).toFloat()

            return Matrix4(
                    cos, sin, 0f, cos * x + sin * y - x,
                    -sin, cos, 0f, -sin * x + cos * y - y,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f)

        }
    }

}

