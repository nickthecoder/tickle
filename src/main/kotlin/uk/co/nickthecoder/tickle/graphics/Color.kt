package uk.co.nickthecoder.tickle.graphics

import uk.co.nickthecoder.tickle.math.Vector3
import uk.co.nickthecoder.tickle.math.Vector4

class Color(r: Float, g: Float, b: Float, a: Float = 1f) {

    val red = Math.max(0f, Math.min(1f, r))
    val green = Math.max(0f, Math.min(1f, g))
    val blue = Math.max(0f, Math.min(1f, b))
    val alpha = Math.max(0f, Math.min(1f, a))

    fun toVector3() = Vector3(red, green, blue)

    fun toVector4f() = Vector4(red, green, blue, alpha)

    override fun toString() = "r=$red g=$green b=$blue a=$alpha"

    companion object {

        val WHITE = Color(1f, 1f, 1f)
        val BLACK = Color(0f, 0f, 0f)
        val RED = Color(1f, 0f, 0f)
        val GREEN = Color(0f, 1f, 0f)
        val BLUE = Color(0f, 0f, 1f)
        val SEMI_TRANSPARENT = Color(1f, 1f, 1f, 0.5f)

        fun create(r: Int, g: Int, b: Int, a: Int) = Color(r.toFloat() / 255f, g.toFloat() / 255f, b.toFloat() / 255f, a.toFloat() / 255f)
    }

}
