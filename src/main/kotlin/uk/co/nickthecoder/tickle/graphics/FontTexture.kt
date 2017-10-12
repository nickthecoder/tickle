package uk.co.nickthecoder.tickle.graphics

/**
 * Holds a texture, and the meta-data for a Font at a particular font-size.
 */
class FontTexture(val texture: Texture, val glyphs: Map<Char, Glyph>, val lineHeight: Double) {

    fun drawOutlined(renderer: Renderer, text: CharSequence, x: Double, y: Double, fill: Color = Color.WHITE, outline: Color = Color.BLACK, thickness: Int) {

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

    fun drawOutlined(renderer: Renderer, text: CharSequence, x: Double, y: Double, fill: Color = Color.WHITE, outline: Color = Color.BLACK) {

        draw(renderer, text, x - 1, y, outline)
        draw(renderer, text, x + 1, y, outline)
        draw(renderer, text, x, y - 1, outline)
        draw(renderer, text, x, y + 1, outline)
        draw(renderer, text, x, y, fill)
    }

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
    }

    fun cleanUp() {
        texture.cleanUp()
    }
}

