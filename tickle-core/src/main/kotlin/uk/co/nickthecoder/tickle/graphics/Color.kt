/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.graphics

import java.util.*

/**
 * NOTE. Color is mutable, in order to prevent lots of Color objects being created, and then thrown away,
 * which would give the garbage collector a hard time, and would lead to lost frames while gc runs.
 *
 */
class Color(r: Float, g: Float, b: Float, a: Float = 1f) {

    constructor() : this(1f, 1f, 1f, 1f)

    var red = Math.max(0f, Math.min(1f, r))
    var green = Math.max(0f, Math.min(1f, g))
    var blue = Math.max(0f, Math.min(1f, b))
    var alpha = Math.max(0f, Math.min(1f, a))

    /**
     * Linear interpolation between two colors.
     */
    fun lerp(other: Color, t: Float, dest: Color): Color {
        val s = 1 - t
        dest.red = red * s + other.red * t
        dest.green = green * s + other.green * t
        dest.blue = blue * s + other.blue * t
        dest.alpha = alpha * s + other.alpha * t
        return dest
    }

    fun mul(other: Color, dest: Color): Color {
        dest.red = red * other.red
        dest.green = green * other.green
        dest.blue = blue * other.blue
        dest.alpha = alpha * other.alpha
        return dest
    }

    fun toHashRGB() = String.format("#%02x%02x%02x", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())

    fun toHashRGBA() = String.format("#%02x%02x%02x%02x", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())

    fun semi(): Color {
        alpha /= 2
        return this
    }

    fun transparent(): Color {
        alpha = 0f
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other is Color) {
            return equals(other)
        }
        return false
    }

    fun equals(other: Color): Boolean {
        return other.red == red && other.green == green && other.blue == blue && other.alpha == alpha
    }

    /**
     * Cheap and cheerful hashCode - haven't tested for likely collisions, but assuming Float.hashCode is
     * good over 0..9, I think it should be ok.
     */
    override fun hashCode(): Int {
        return Objects.hash(red, 2 + green, 4 + blue, 6 + green, 8 + alpha)
    }

    fun set(r: Float, g: Float, b: Float, a: Float) {
        red = r
        green = g
        blue = b
        alpha = a
    }

    fun set(other: Color) {
        red = other.red
        green = other.green
        blue = other.blue
        alpha = other.alpha
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

        fun white() = Color(1f, 1f, 1f)
        fun black() = Color(0f, 0f, 0f)
        fun red() = Color(1f, 0f, 0f)
        fun green() = Color(0f, 1f, 0f)
        fun blue() = Color(0f, 0f, 1f)


        fun create(r: Int, g: Int, b: Int, a: Int) = Color(r.toFloat() / 255f, g.toFloat() / 255f, b.toFloat() / 255f, a.toFloat() / 255f)
    }

}
