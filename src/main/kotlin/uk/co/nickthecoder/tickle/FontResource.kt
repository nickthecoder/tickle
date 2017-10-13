package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.FontTexture
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT
import java.awt.Font
import java.io.File

class FontResource(var xPadding: Int = 1, var yPadding: Int = 1) {

    constructor(fontName: String, style: FontStyle, size: Double, xPadding: Int = 1, yPadding: Int = 1) : this(xPadding, yPadding) {
        this.fontName = fontName
        this.style = style
        this.size = size
    }

    constructor(file: File, size: Double, xPadding: Int = 1, yPadding: Int = 1) : this(xPadding, yPadding) {
        this.file = file
        this.size = size
    }

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
        return FontTextureFactoryViaAWT(font, xPadding = xPadding, yPadding = yPadding).create()
    }

    enum class FontStyle { PLAIN, BOLD, ITALIC, BOLD_ITALIC }

}
