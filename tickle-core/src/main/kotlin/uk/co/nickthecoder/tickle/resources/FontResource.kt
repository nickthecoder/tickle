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
package uk.co.nickthecoder.tickle.resources

import uk.co.nickthecoder.tickle.graphics.FontTexture
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Deletable
import uk.co.nickthecoder.tickle.util.Dependable
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

    var pngFile: File? = null
        set(v) {
            field = v
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

    fun reload() {
        pngFile?.let { pngFile ->
            loadFromFile(pngFile)
        }
    }

    fun clearCache() {
        cached = null
    }

    fun loadFromFile(pngFile: File) {
        val texture = Texture.create(pngFile)
        val metricsFile = File(pngFile.parentFile, pngFile.nameWithoutExtension + ".metrics")
        fontTexture = JsonResources.loadFontMetrics(metricsFile, texture)
        this.pngFile = pngFile

        val outlineFile = File(pngFile.parentFile, pngFile.nameWithoutExtension + "-outline.png")
        if (outlineFile.exists()) {
            val outlineTexture = Texture.create(outlineFile)
            outlineFontTexture = FontTexture(JsonResources.copyGlyphs(outlineTexture, fontTexture.glyphs), fontTexture.lineHeight,
                    leading = fontTexture.leading, ascent = fontTexture.ascent, descent = fontTexture.descent)
        }
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

    override fun dependables(): List<Dependable> {
        return Resources.instance.costumes.items().values.filter { it.dependsOn(this) }
    }

    override fun delete() {
        Resources.instance.fontResources.remove(this)
    }

    fun destroy() {
        fontTexture.destroy()
    }

    override fun rename(newName: String) {
        Resources.instance.fontResources.rename(this, newName)
    }

    enum class FontStyle { PLAIN, BOLD, ITALIC, BOLD_ITALIC }

}
