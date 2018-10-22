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

import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.util.YDownRect
import java.awt.Font
import java.awt.FontMetrics
import java.awt.RenderingHints
import java.awt.image.BufferedImage


/**
 *
 * Note. Some fonts may extend higher than their ascent, or lower than their descent. In such cases, padding is
 * required around each glyph to prevent one glyph overlapping another.
 */
open class FontTextureFactoryViaAWT(
        val font: Font,
        val xPadding: Int = 1,
        val yPadding: Int = 1,
        val maxTextureWidth: Int = (font.size + xPadding * 2) * 12) {

    protected val glyphDataMap = mutableMapOf<Char, GlyphData>()

    protected var requiredWidth: Int = 0

    protected var requiredHeight: Int = 0

    /**
     * Only used to gather font metrics
     */
    private val prepareImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

    private val prepareG = prepareImage.createGraphics()

    protected val metrics: FontMetrics

    /** The next position to place a glyph in the texture.*/
    private var nextX: Int = 0

    private var nextY: Int = 0

    init {
        prepareG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        prepareG.font = font
        metrics = prepareG.fontMetrics
        requiredHeight = metrics.height
    }

    fun range(from: Char, to: Char): FontTextureFactoryViaAWT {
        return range(from.toInt(), to.toInt())
    }

    fun range(from: Int = 32, to: Int = 255): FontTextureFactoryViaAWT {
        for (i in from..to) {
            val c = i.toChar()

            if (font.canDisplay(c)) {

                val cWidth = metrics.charWidth(c)
                val advance = metrics.charWidth(c).toDouble()

                if (nextX + cWidth + xPadding * 2 > maxTextureWidth) {
                    nextX = 0
                    nextY += metrics.height + yPadding * 2
                }

                val glyphData = GlyphData(cWidth.toDouble() + xPadding * 2, advance, nextX, nextY)

                nextX += cWidth + xPadding * 2

                if (nextX > requiredWidth) {
                    requiredWidth = nextX
                }
                glyphDataMap[c] = glyphData
            }

        }
        requiredHeight = nextY

        return this
    }

    open fun create(): FontTexture {
        // If no ranges were specified, use the default range of 32..255
        if (glyphDataMap.isEmpty()) {
            range()
        }

        val bufferedImage = BufferedImage(requiredWidth, requiredHeight, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics()
        g.font = font
        g.paint = java.awt.Color.WHITE

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        glyphDataMap.forEach { c, glyphData ->
            g.drawString(c.toString(), glyphData.x + xPadding.toInt(), glyphData.y + metrics.ascent + yPadding.toInt())
        }

        /* Get pixel data of image */
        val pixels = IntArray(requiredWidth * requiredHeight)
        bufferedImage.getRGB(0, 0, requiredWidth, requiredHeight, pixels, 0, requiredWidth)

        /* Put pixel data into a ByteBuffer, ensuring it is in RGBA order */
        val buffer = MemoryUtil.memAlloc(requiredWidth * requiredHeight * 4)
        for (y in 0..requiredHeight - 1) {
            for (x in 0..requiredWidth - 1) {
                /* Pixel format is : 0xAARRGGBB */
                val pixel = pixels[y * requiredWidth + x]
                buffer.put((pixel shr 16 and 0xFF).toByte()) // R
                buffer.put((pixel shr 8 and 0xFF).toByte())  // G
                buffer.put((pixel and 0xFF).toByte())        // B
                buffer.put((pixel shr 24 and 0xFF).toByte()) // A
            }
        }

        buffer.flip()

        val texture = Texture(requiredWidth, requiredHeight, GL11.GL_RGBA, buffer)
        MemoryUtil.memFree(buffer)

        val glyphs = mutableMapOf<Char, Glyph>()
        glyphDataMap.forEach { c, glyphData ->

            val rect = YDownRect(
                    glyphData.x,
                    glyphData.y,
                    glyphData.x + glyphData.width.toInt(),
                    glyphData.y + metrics.height + yPadding * 2)

            val pose = Pose(texture, rect)
            pose.offsetX = xPadding.toDouble()
            pose.offsetY = metrics.height + yPadding.toDouble()
            glyphs[c] = Glyph(pose, glyphData.advance)
        }

        return FontTexture(
                glyphs, metrics.height.toDouble(),
                leading = metrics.leading.toDouble(),
                ascent = metrics.ascent.toDouble(),
                descent = metrics.descent.toDouble())
    }

}

data class GlyphData(
        /** The width of this glyph (i.e. the width of the bounding rectangle which encompases the glyph */
        val width: Double,
        /** The starting position of the NEXT glyph relative to this one when rendering text. This of course
         * does not take into accound kerning. Kerning is not supported by the fonts in Tickle.
         */
        val advance: Double,
        /** The position of the top of the glyph within the texture image */
        val x: Int,
        /** The position of the left of the glyph within the texture image */
        val y: Int)