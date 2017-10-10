package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.util.YDownRect
import java.awt.Font
import java.awt.FontMetrics
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

/**
 * Holds a texture, and the meta-data for a Font at a particular font-size.
 */
class FontTexture(val texture: Texture, val glyphs: Map<Char, Glyph>, val lineHeight: Int) {

    fun draw(renderer: Renderer, text: CharSequence, x: Double, y: Double, color: Color = Color.WHITE) {
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
                glyph.pose.draw(renderer, drawX, drawY, color)
                drawX += glyph.advance
            }
        }
        renderer.end()
    }

    fun cleanUp() {
        texture.cleanUp()
    }
}


class FontTextureFactory(val font: Font, val antiAlias: Boolean, val maxTextureWidth : Int = font.size * 20) {

    private val glyphDataMap = mutableMapOf<Char, GlyphData>()

    private var requiredWidth: Int = 0

    private var requiredHeight: Int = 0

    /**
     * Only used to gather font metrics
     */
    private val prepareImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

    private val prepareG = prepareImage.createGraphics()

    private val metrics: FontMetrics

    /** The next position to place a glyph in the texture.*/
    private var nextX: Int = maxTextureWidth + 1 // Force the first character to create a new line

    private var nextY: Int = 0

    private var lineHeight: Int = 0

    init {
        if (antiAlias) {
            prepareG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        prepareG.font = font
        metrics = prepareG.fontMetrics
    }

    fun range(from: Char, to: Char): FontTextureFactory {
        return range(from.toInt(), to.toInt())
    }

    fun range(from: Int = 32, to: Int = 255): FontTextureFactory {
        for (i in from..to) {
            val c = i.toChar()

            if (font.canDisplay(c) ) {

                // Width that the character will take up in the texture. BUG. This is the ADVANCE isn't it?
                val bounds: Rectangle2D = metrics.getStringBounds(c.toString(), prepareG)
                val advance = metrics.charWidth(c)

                if (bounds.height + 1 > lineHeight) {
                    lineHeight = bounds.height.toInt() + 1
                }

                if (nextX + bounds.width > maxTextureWidth) {
                    nextX = 0
                    nextY += lineHeight
                    lineHeight = 0
                }

                val glyphData = GlyphData(bounds, advance, nextX, nextY)

                nextX += bounds.width.toInt() + 1

                if (nextX > requiredWidth) {
                    requiredWidth = nextX
                }
                if (nextY > requiredHeight) {
                    requiredHeight = nextY
                }
                // println("$c = $glyphData")
                glyphDataMap[c] = glyphData
            }

        }
        return this
    }

    fun create(): FontTexture {
        // IF no ranges were specified, use the default range of 32..255
        if (glyphDataMap.isEmpty()) {
            range()
        }

        val bufferedImage = BufferedImage(requiredWidth, requiredHeight, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics()
        g.font = font
        //g.paint = java.awt.Color.GREEN
        //g.fillRect(0, 0, requiredWidth, requiredHeight)
        g.paint = java.awt.Color.WHITE

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }

        glyphDataMap.forEach { c, glyphData ->
            // println("Drawing glyph $c ${glyphData.x},${(glyphData.y - glyphData.bounds.y).toInt()}")
            g.drawString(c.toString(), glyphData.x, (glyphData.y - (glyphData.bounds.height + glyphData.bounds.y)).toInt())
        }

        /* Flip image to get the origin at the bottom left */

        val transform = AffineTransform.getScaleInstance(1.0, -1.0)
        transform.translate(0.0, (-requiredHeight).toDouble())
        val operation = AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        val flippedImage = operation.filter(bufferedImage, null)

        /* Get pixel data of image */
        val pixels = IntArray(requiredWidth * requiredHeight)
        flippedImage.getRGB(0, 0, requiredWidth, requiredHeight, pixels, 0, requiredWidth)

        /* Put pixel data into a ByteBuffer, ensuring it is in RGBA order */
        val buffer = MemoryUtil.memAlloc(requiredWidth * requiredHeight * 4)
        for (y in 0..requiredHeight - 1) {
            for (x in 0..requiredWidth - 1) {
                /* Pixel format is : 0xAARRGGBB */
                val pixel = pixels[y * requiredWidth + x]
                buffer.put((pixel shr 16 and 0xFF).toByte())
                buffer.put((pixel shr 8 and 0xFF).toByte())
                buffer.put((pixel and 0xFF).toByte())
                buffer.put((pixel shr 24 and 0xFF).toByte())
            }
        }

        buffer.flip()

        val texture = Texture(requiredWidth, requiredHeight, GL11.GL_RGBA, buffer)
        MemoryUtil.memFree(buffer)

        val glyphs = mutableMapOf<Char, Glyph>()
        glyphDataMap.forEach { c, glyphData ->
            val rect = YDownRect(glyphData.x, glyphData.y, glyphData.x + glyphData.bounds.width.toInt(), glyphData.y - glyphData.bounds.height.toInt())
            val pose = Pose(texture, rect)
            pose.offsetX = -glyphData.bounds.x
            pose.offsetY = glyphData.bounds.y
            glyphs[c] = Glyph(pose, glyphData.advance)
            // println("Created Glyph $c : ${rect} offset= ${pose.offsetX},${pose.offsetY} advance=${glyphData.advance}")
        }

        return FontTexture(texture, glyphs, metrics.height)
    }

    data class GlyphData(val bounds: Rectangle2D, val advance: Int, val x: Int, val y: Int)
}
