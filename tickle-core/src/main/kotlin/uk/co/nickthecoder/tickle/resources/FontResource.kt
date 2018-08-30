package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.graphics.FontTexture
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.JsonResources
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

    private var pngFile: File? = null
    private var outlineFile: File? = null
    private var metricsFile: File? = null

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

    fun reload() {
        pngFile?.let { pngFile ->
            metricsFile?.let { metricsFile ->
                loadFromFile(pngFile, metricsFile)
            }
        }
        outlineFile?.let {
            loadOutline(it)
        }
    }

    fun clearCache() {
        cached = null
    }

    fun loadFromFile(pngFile: File, metricsFile: File) {
        val texture = Texture.create(pngFile)
        fontTexture = JsonResources.loadFontMetrics(metricsFile, texture)
        this.pngFile = pngFile
        this.metricsFile = metricsFile
    }

    fun loadOutline(outlinePngFile: File) {
        val outlineTexture = Texture.create(outlinePngFile)
        outlineFontTexture = FontTexture(JsonResources.copyGlyphs(outlineTexture, fontTexture.glyphs), fontTexture.lineHeight,
                leading = fontTexture.leading, ascent = fontTexture.ascent, descent = fontTexture.descent)
        this.outlineFile = outlinePngFile
    }

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

    override fun usedBy(): Any? {
        return Resources.instance.costumes.items().values.firstOrNull { it.uses(this) }
    }

    override fun delete() {
        Resources.instance.fontResources.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.fontResources.rename(this, newName)
    }

    enum class FontStyle { PLAIN, BOLD, ITALIC, BOLD_ITALIC }

}
