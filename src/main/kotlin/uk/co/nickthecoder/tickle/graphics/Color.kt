package uk.co.nickthecoder.tickle.graphics

import java.nio.FloatBuffer

class Color(r: Float, g: Float, b: Float, a: Float = 1f) {

    val red = Math.max(0f, Math.min(1f, r))
    val green = Math.max(0f, Math.min(1f, g))
    val blue = Math.max(0f, Math.min(1f, b))
    val alpha = Math.max(0f, Math.min(1f, a))

    fun intoBuffer(buffer: FloatBuffer) {
        buffer.put(red).put(green).put(blue).put(alpha)
        buffer.flip()
    }

    fun linearInterpolation(other: Color, t: Float): Color {
        val s = 1 - t
        return Color(
                red * s + other.red * t,
                green * s + other.green * t,
                blue * s + other.blue * t,
                alpha * s + other.alpha * t)
    }

    override fun toString() = "r=$red g=$green b=$blue a=$alpha"

    companion object {

        fun createFromHSB(hue: Float, saturation: Float, brightness: Float): Color {
            val awtColor = java.awt.Color(java.awt.Color.HSBtoRGB(hue, saturation, brightness))

            return Color(awtColor.red / 255f, awtColor.green / 255f, awtColor.blue / 255f)
        }

        fun fromString(str: String): Color {
            var color: String = str

            if (str.startsWith("#")) run {
                color = color.substring(1)
            }
            val len = color.length
            val r: Int
            val g: Int
            val b: Int
            val a: Int

            try {
                if (len == 3) {
                    r = Integer.parseInt(color.substring(0, 1), 16)
                    g = Integer.parseInt(color.substring(1, 2), 16)
                    b = Integer.parseInt(color.substring(2, 3), 16)
                    return Color(r / 15f, g / 15f, b / 15f, 1f)
                } else if (len == 4) {
                    r = Integer.parseInt(color.substring(0, 1), 16)
                    g = Integer.parseInt(color.substring(1, 2), 16)
                    b = Integer.parseInt(color.substring(2, 3), 16)
                    a = Integer.parseInt(color.substring(3, 4), 16)
                    return Color(r / 15f, g / 15f, b / 15f, a / 15f)
                } else if (len == 6) {
                    r = Integer.parseInt(color.substring(0, 2), 16)
                    g = Integer.parseInt(color.substring(2, 4), 16)
                    b = Integer.parseInt(color.substring(4, 6), 16)
                    return Color(r / 255f, g / 255f, b / 255f, 1f)
                } else if (len == 8) {
                    r = Integer.parseInt(color.substring(0, 2), 16)
                    g = Integer.parseInt(color.substring(2, 4), 16)
                    b = Integer.parseInt(color.substring(4, 6), 16)
                    a = Integer.parseInt(color.substring(6, 8), 16)
                    return Color(r / 255f, g / 255f, b / 255f, a / 255f)
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Not a valid color string", e)
            }
            throw IllegalArgumentException("Not a valid color string")
        }

        val WHITE = Color(1f, 1f, 1f)
        val BLACK = Color(0f, 0f, 0f)
        val RED = Color(1f, 0f, 0f)
        val GREEN = Color(0f, 1f, 0f)
        val BLUE = Color(0f, 0f, 1f)
        val SEMI_TRANSPARENT = Color(1f, 1f, 1f, 0.5f)

        val TRANSPARENT_WHITE = Color(1f, 1f, 1f, 0.0f)
        val TRANSPARENT_BLACK = Color(0f, 0f, 0f, 0.0f)

        fun create(r: Int, g: Int, b: Int, a: Int) = Color(r.toFloat() / 255f, g.toFloat() / 255f, b.toFloat() / 255f, a.toFloat() / 255f)
    }

}
