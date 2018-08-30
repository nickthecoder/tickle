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
package uk.co.nickthecoder.tickle.sandbox

import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextHAlignment
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.graphics.TextVAlignment
import uk.co.nickthecoder.tickle.resources.FontResource
import java.awt.Font

/**
 * Creates a FontTexture, and draws it in a window.
 * The whole texture is drawn semi-transparent, and some specific lines of text are draw full opaque over the top.
 */
class FontSandbox : Sandbox(height = 700) {

    val xPadding = 4
    val yPadding = 4

    val fontResource = FontResource(Font.SANS_SERIF, FontResource.FontStyle.PLAIN, 22.0, xPadding, yPadding)

    val font = fontResource.fontTexture

    val pose = Pose(font.glyphs.values.first().pose.texture)

    var x = 10.0
    var y = 10.0

    init {
        renderer.clearColor(Color.blue())
    }

    override fun tick() {
        renderer.beginView()
        renderer.clear()

        val semiBlack = Color.black().semi()
        val white = Color.white()

        pose.draw(renderer, 250.0, 400.0, Color.white().semi())

        val text = ".oO° Hello Worldy °Oo."

        font.draw(renderer, "$text - Plain", 20.0, 100.0)

        font.drawOutlined(renderer, "$text - Semi-Transparent Outline", 20.0, 130.0, outline = semiBlack)
        font.drawOutlined(renderer, "$text - Outlined", 20.0, 160.0, outline = semiBlack)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 3, alpha 0.3", 20.0, 190.0, outline = Color(0.0f, 0.0f, 0.0f, 0.3f), thickness = 3)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 6, alpha 0.05", 20.0, 220.0, outline = Color(0.0f, 0.0f, 0.0f, 0.05f), thickness = 6)
        font.drawOutlined(renderer, "$text - Blurred outline, thickness 6, alpha 0.025", 20.0, 250.0, outline = Color(0.0f, 0.0f, 0.0f, 0.025f), thickness = 6)

        val topLeft = TextStyle(fontResource, TextHAlignment.LEFT, TextVAlignment.TOP, white)
        val topRight = TextStyle(fontResource, TextHAlignment.RIGHT, TextVAlignment.TOP, white)
        val bottomLeft = TextStyle(fontResource, TextHAlignment.LEFT, TextVAlignment.BOTTOM, white)
        val bottomRight = TextStyle(fontResource, TextHAlignment.RIGHT, TextVAlignment.BOTTOM, white)
        val baselineCenter = TextStyle(fontResource, TextHAlignment.CENTER, TextVAlignment.BASELINE, white)
        val center = TextStyle(fontResource, TextHAlignment.CENTER, TextVAlignment.CENTER, white)

        topLeft.draw(renderer, "Top\nLeft", 0.0, window.height.toDouble())
        topRight.draw(renderer, "Top\nRight\n", window.width.toDouble(), window.height.toDouble())
        bottomLeft.draw(renderer, "Bottom\nLeft", 0.0, 0.0)
        bottomRight.draw(renderer, "Bottom\nRight", window.width.toDouble(), 0.0)
        baselineCenter.draw(renderer, "Baseline\nCenter\njqy", window.width.toDouble() / 2, 0.0)
        center.draw(renderer, "Center\nCenter\njqy", window.width.toDouble() / 2, window.height.toDouble() / 2)

        val exampleGlyph = font.glyphs.values.first()
        println("Glyph pose ${exampleGlyph.pose}")
        renderer.endView()
        window.swap()
    }
}

fun main(args: Array<String>) {
    FontSandbox().loop()
}
