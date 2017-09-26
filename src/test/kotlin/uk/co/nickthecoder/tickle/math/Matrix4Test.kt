package uk.co.nickthecoder.tickle.math

import org.junit.Assert.assertEquals
import org.junit.Test

class Matrix4Test {

    @Test
    fun indentity() {
        val identity = Matrix4()
        val e = Matrix4(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f)
        assertEquals(e, identity)
    }

    @Test
    fun M4Constructor() {
        val m = Matrix4(
                1f, 2f, 3f, 4f,
                5f, 6f, 7f, 8f,
                9f, 10f, 11f, 12f,
                13f, 14f, 15f, 16f)
        assertEquals(m.m00, 1f)
        assertEquals(m.m10, 2f)
        assertEquals(m.m20, 3f)
        assertEquals(m.m30, 4f)

        assertEquals(m.m01, 5f)
        assertEquals(m.m11, 6f)
        assertEquals(m.m21, 7f)
        assertEquals(m.m31, 8f)

        assertEquals(m.m02, 9f)
        assertEquals(m.m12, 10f)
        assertEquals(m.m22, 11f)
        assertEquals(m.m32, 12f)

        assertEquals(m.m03, 13f)
        assertEquals(m.m13, 14f)
        assertEquals(m.m23, 15f)
        assertEquals(m.m33, 16f)
    }

    @Test
    fun translate() {
        val t = Matrix4.translate(2f, 3f, 4f)
        val e = Matrix4(m30 = 2f, m31 = 3f, m32 = 4f)
        assertEquals(e, t)
    }

    @Test
    fun translateTimesIdentity() {
        val t1 = Matrix4.translate(2f, 3f, 4f)
        val identity = Matrix4()

        val t = t1 * identity
        assertEquals(t1, t)
    }

    @Test
    fun identityTimesTranslate() {
        val t1 = Matrix4.translate(2f, 3f, 4f)
        val identity = Matrix4()
        val t = identity * t1
        assertEquals(t1, t)
    }

    @Test
    fun translateTwice() {
        val t1 = Matrix4.translate(2f, 3f, 4f)
        val t2 = Matrix4.translate(10f, 20f, 30f)
        val t = t1 * t2
        val e = Matrix4(m30 = 12f, m31 = 23f, m32 = 34f)
        assertEquals(e, t)
    }
}