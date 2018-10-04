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

import org.joml.Matrix4f

/**
 * Holds a texture, and the meta-data for a Font at a particular font-size.
 */
class FontTexture(
        val glyphs: Map<Char, Glyph>,
        val lineHeight: Double,
        val leading: Double,
        val ascent: Double,
        val descent: Double) {

    private val WHITE = Color.white()

    private val BLACK = Color.black()

    fun drawOutlined(renderer: Renderer, text: CharSequence, x: Double, y: Double, fill: Color = WHITE, outline: Color = BLACK, thickness: Int) {

        val t2 = thickness * thickness
        for (dx in -thickness..thickness) {
            for (dy in -thickness..thickness) {
                if (dx * dx + dy * dy < t2) {
                    draw(renderer, text, x + dx, y + dy, outline)
                }
            }
        }
        draw(renderer, text, x, y, fill)
    }

    fun drawOutlined(renderer: Renderer, text: CharSequence, x: Double, y: Double, fill: Color = WHITE, outline: Color = BLACK) {

        draw(renderer, text, x - 1, y, outline)
        draw(renderer, text, x + 1, y, outline)
        draw(renderer, text, x, y - 1, outline)
        draw(renderer, text, x, y + 1, outline)
        draw(renderer, text, x, y, fill)
    }

    fun draw(renderer: Renderer, text: CharSequence, x: Double, y: Double, color: Color = WHITE, modelMatrix: Matrix4f? = null) {
        var drawX = x
        var drawY = y

        for (i in 0..text.length - 1) {
            val ch = text[i]
            if (ch == '\n') {
                /* Line feed, set x and y to draw at the next line */
                drawY -= lineHeight
                drawX = x
                continue
            }
            if (ch == '\r') {
                /* Carriage return, just skip it */
                continue
            }
            glyphs[ch]?.let { glyph ->
                glyph.pose.draw(renderer, drawX, drawY, color, modelMatrix)
                drawX += glyph.advance
            }
        }
    }

    fun lineWidth(text: String): Double {
        if (text.isEmpty()) return 0.0
        if (text.length == 1) return (glyphs[text[0]]?.width)?.toDouble() ?: 0.0

        return text.sumByDouble { glyphs[it]?.advance ?: 0.0 }
    }

    fun width(text: CharSequence): Double {
        var max = 0.0
        text.split('\n').forEach { line ->
            val lw = lineWidth(line)
            if (lw > max) max = lw
        }
        return max
    }

    fun height(text: CharSequence): Double {
        val extra = if (text.endsWith('\n')) 0 else 1
        return (text.count { it == '\n' } + extra) * lineHeight
    }

    fun destroy() {
        glyphs.values.firstOrNull()?.pose?.texture?.destroy()
    }

}

