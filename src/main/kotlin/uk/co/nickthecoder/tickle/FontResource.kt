package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.FontTexture
import uk.co.nickthecoder.tickle.graphics.FontTextureFactoryViaAWT

interface FontResource {

    val fontTexture: FontTexture

    val name: String

    val size: Double

}

class NamedFontResource(override var name: String) : FontResource {

    private var cached: FontTexture? = null

    var fontName: String = name
        set(v) {
            field = v
            cached = null
        }

    var style: FontStyle = FontStyle.PLAIN
        set(v) {
            field = v
            cached = null
        }

    override var size: Double = 22.0
        set(v) {
            field = v
            cached = null
        }

    override val fontTexture: FontTexture
        get() {
            cached?.let { return it }
            val c = FontTextureFactoryViaAWT(java.awt.Font(fontName, style.ordinal, size.toInt())).create()
            cached = c
            return c
        }

    enum class FontStyle { PLAIN, BOLD, ITALIC, BOLD_ITALIC }

}
