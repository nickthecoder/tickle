package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.graphics.FontTexture
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Renamable
import java.awt.Font
import java.io.File

class FontResource(var xPadding: Int = 1, var yPadding: Int = 1)

    : Deletable, Renamable {

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

    var fontTexture: FontTexture
        get() {
            cached?.let { return it }
            val c = createFontTexture()
            cached = c
            return c
        }
        set(v) {
            cached = v
        }

    var outlineFontTexture: FontTexture? = null

    private fun createFontTexture(): FontTexture {
        val font: Font
        if (file == null) {
            font = Font(fontName, style.ordinal, size.toInt())
        } else {
            val loadedFont = Font.createFont(java.awt.Font.TRUETYPE_FONT, file)
            font = loadedFont.deriveFont(size.toFloat())
        }
        return FontTextureFactoryViaAWT(font, xPadding = xPadding, yPadding = yPadding).create()
    }


    override fun delete() {
        Resources.instance.fontResources.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.fontResources.rename(this, newName)
    }

    enum class FontStyle { PLAIN, BOLD, ITALIC, BOLD_ITALIC }

}
