package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.FontTexture
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT
import java.awt.Font
import java.io.File

class FontResource() {

    private var cached: FontTexture? = null

    var file: File? = null
        set(v) {
            if (field != v) {
                field = v
                cached = null
            }
        }

    var fontName: String = java.awt.Font.SANS_SERIF
        set(v) {
            if (field != v) {
                field = v
                cached = null
            }
        }

    var style: FontStyle = FontStyle.PLAIN
        set(v) {
            if (field != v) {
                field = v
                cached = null
            }
        }

    var size: Double = 22.0
        set(v) {
            if (field != v) {
                field = v
                cached = null
            }
        }

    val fontTexture: FontTexture
        get() {
            cached?.let { return it }
            val c = createFontTexture()
            cached = c
            return c
        }

    fun createFontTexture(): FontTexture {
        val font: Font
        if (file == null) {
            font = Font(fontName, style.ordinal, size.toInt())
        } else {
            val loadedFont = Font.createFont(java.awt.Font.TRUETYPE_FONT, file)
            font = loadedFont.deriveFont(size.toFloat())
        }
        return FontTextureFactoryViaAWT(font).create()
    }

    enum class FontStyle { PLAIN, BOLD, ITALIC, BOLD_ITALIC }

}
