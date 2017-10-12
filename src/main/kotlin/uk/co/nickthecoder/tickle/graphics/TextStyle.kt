package uk.co.nickthecoder.tickle.graphics

import uk.co.nickthecoder.tickle.FontResource

class TextStyle(
        var fontResource: FontResource,
        var halignment: HAlignment,
        var valignment: VAlignment,
        var color: Color) {

    fun offsetX(text: String): Double {
        return when (halignment) {
            HAlignment.LEFT -> 0.0
            HAlignment.RIGHT -> width(text)
            HAlignment.CENTER -> width(text) / 2.0
        }
    }

    fun offsetY(text: String): Double {
        // TODO NONE of these are correct yet! Need descent data, and multi-line is tricky!
        // Currently the glyph's offsets are based on the BASELINE, which isn't useful in most cases. Hmmm.
        return when (valignment) {
            VAlignment.TOP -> height(text)
            VAlignment.BOTTOM -> 0.0
            VAlignment.CENTER -> height(text) / 2.0
            VAlignment.BASELINE -> 0.0
        }
    }

    fun singleLineWidth(line: String): Double {
        return line.sumByDouble { fontResource.fontTexture.glyphs[it]?.advance ?: 0.0 }
    }

    fun width(text: String): Double {
        return text.split('\n').map { singleLineWidth(it) }.max() ?: 0.0
    }

    fun height(text: String): Double {
        val lineCount = text.filter { it == '\n' }.count() + 1
        return fontResource.fontTexture.lineHeight * lineCount
    }

    fun draw(renderer: Renderer, text: CharSequence, x: Double, y: Double, color: Color = Color.WHITE) {
        fontResource.fontTexture.draw(renderer, text, x, y, color)
    }
}

enum class HAlignment { LEFT, CENTER, RIGHT }
enum class VAlignment { BOTTOM, BASELINE, CENTER, TOP }
