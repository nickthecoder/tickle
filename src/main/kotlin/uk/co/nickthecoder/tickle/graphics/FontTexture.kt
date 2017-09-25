package uk.co.nickthecoder.tickle.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil

import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

/**
 * Holds a texture, and the meta-data for a Font at a particular font-size.
 */
class FontTexture(val texture: Texture, val glyphs: Map<Char, Glyph>) {

    fun textSize(text: CharSequence): Pair<Int, Int> {
        var width = 0
        var height = 0

        var lineWidth = 0
        var lineHeight = 0

        for (i in 0..text.length - 1) {
            val c = text[i]
            if (c == '\n') {
                width = Math.max(width, lineWidth)
                lineWidth = 0
                height += lineHeight
                lineHeight = 0
                continue
            }
            if (c == '\r') {
                continue
            }
            glyphs[c]?.let { glyph ->
                lineWidth += glyph.width
                lineHeight = Math.max(lineHeight, glyph.height)
            }
        }
        width = Math.max(width, lineWidth)
        return Pair(width, height)
    }

    /*
    fun render(renderer: Renderer, text: CharSequence, x: Float, y: Float, c: Color = Color.WHITE) {
        val textHeight = getHeight(text)

        var drawX = x
        var drawY = y
        if (textHeight > fontHeight) {
            drawY += (textHeight - fontHeight).toFloat()
        }

        texture.bind()
        renderer.begin()
        for (i in 0..text.length - 1) {
            val ch = text[i]
            if (ch == '\n') {
                /* Line feed, set x and y to draw at the next line */
                drawY -= fontHeight.toFloat()
                drawX = x
                continue
            }
            if (ch == '\r') {
                /* Carriage return, just skip it */
                continue
            }
            glyphs[ch]?.let { glyph ->
                renderer.drawTextureRegion(texture, drawX, drawY, glyph.x, glyph.y, glyph.width, glyph.height, c)
                drawX += glyph.width
            }
        }
        renderer.end()
    }

    */

    fun cleanUp() {
        texture.cleanUp()
    }

    companion object {

        public fun create(font: Font, antiAlias: Boolean = true): FontTexture {

            // First, iterate over all of the characters we need to find the required width and height of
            // the image which will contain ALL of the rendered characters.

            var width = 0
            var height = 0

            // Character 0..31 are non-printable, so ignore those.
            for (i in 32..255) {
                val c = i.toChar()
                /* If bufferedImage image is null the font does not contain that chararacter */
                val bufferedImage = createGlyphImage(font, c, antiAlias) ?: continue

                width += bufferedImage.width
                height = Math.max(height, bufferedImage.height)
            }

            /* Image for the texture */
            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val g = bufferedImage.createGraphics()
            val glyphs = mutableMapOf<Char, Glyph>()

            var x = 0

            for (i in 32..255) {
                val c = i.toChar()
                /* If char image is null that font does not contain the char */
                val charImage = createGlyphImage(font, c, antiAlias) ?: continue

                val charWidth = charImage.width
                val charHeight = charImage.height

                val glyph = Glyph(charWidth, charHeight, x, bufferedImage.height - charHeight, 0f)
                g.drawImage(charImage, x, 0, null)
                x += glyph.width
                glyphs.put(c, glyph)
            }

            /* Flip image to get the origin at the bottom left */
            val transform = AffineTransform.getScaleInstance(1.0, -1.0)
            transform.translate(0.0, (-height).toDouble())
            val operation = AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
            val flippedImage = operation.filter(bufferedImage, null)


            /* Get pixel data of image */
            val pixels = IntArray(width * height)
            flippedImage.getRGB(0, 0, width, height, pixels, 0, width)

            /* Put pixel data into a ByteBuffer, ensuring it is in RGBA order */
            val buffer = MemoryUtil.memAlloc(width * height * 4)
            for (x in 0..width - 1) {
                for (y in 0..height - 1) {
                    /* Pixel format is : 0xAARRGGBB */
                    val pixel = pixels[y * width + x]
                    buffer.put((pixel shr 16 and 0xFF).toByte())
                    buffer.put((pixel shr 8 and 0xFF).toByte())
                    buffer.put((pixel and 0xFF).toByte())
                    buffer.put((pixel shr 24 and 0xFF).toByte())
                }
            }
            buffer.flip()

            val texture = Texture(width, height, GL11.GL_RGBA, buffer)
            MemoryUtil.memFree(buffer)

            return FontTexture(texture, glyphs)
        }


        private fun createGlyphImage(font: Font, c: Char, antiAlias: Boolean): BufferedImage? {
            /* Creating temporary image to extract character size */
            var image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            var g = image.createGraphics()
            if (antiAlias) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            }
            g.font = font
            val metrics = g.fontMetrics
            g.dispose()

            /* Get char charWidth and charHeight */
            val charWidth = metrics.charWidth(c)
            val charHeight = metrics.height

            /* Check if charWidth is 0 */
            if (charWidth == 0) {
                return null
            }

            /* Create image for holding the char */
            image = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)
            g = image.createGraphics()
            if (antiAlias) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            }
            g.font = font
            g.paint = java.awt.Color.WHITE
            g.drawString(c.toString(), 0, metrics.ascent)
            g.dispose()
            return image
        }

    }

}
