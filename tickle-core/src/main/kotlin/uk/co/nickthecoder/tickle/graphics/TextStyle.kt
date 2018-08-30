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
package uk.co.nickthecoder.tickle.graphics

import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.resources.FontResource

class TextStyle(
        var fontResource: FontResource,
        var halignment: TextHAlignment,
        var valignment: TextVAlignment,
        var color: Color,
        var outlineColor: Color? = null) {

    fun offsetX(text: CharSequence): Double {
        return when (halignment) {
            TextHAlignment.LEFT -> 0.0
            TextHAlignment.RIGHT -> width(text)
            TextHAlignment.CENTER -> width(text) / 2.0
        }
    }

    fun offsetY(text: CharSequence): Double {
        return when (valignment) {
            TextVAlignment.TOP -> 0.0
            TextVAlignment.CENTER -> height(text) / 2.0
            TextVAlignment.BASELINE -> height(text) - fontResource.fontTexture.descent
            TextVAlignment.BOTTOM -> height(text)
        }
    }

    fun width(text: CharSequence) = fontResource.fontTexture.width(text)

    fun height(text: CharSequence) = fontResource.fontTexture.height(text)

    fun draw(renderer: Renderer, text: CharSequence, x: Double, y: Double) {
        if (outlineColor != null && fontResource.outlineFontTexture != null) {
            draw(renderer, fontResource.outlineFontTexture!!, outlineColor!!, text, x, y)
        }
        draw(renderer, fontResource.fontTexture, color, text, x, y)
    }

    private val tempColor = Color.white()

    fun draw(renderer: Renderer, text: CharSequence, actor: Actor) {
        val modelMatrix: Matrix4f?

        if (actor.isSimpleImage()) {
            modelMatrix = null
        } else {
            modelMatrix = actor.calculateModelMatrix()
        }

        if (outlineColor != null && fontResource.outlineFontTexture != null) {

            draw(renderer, fontResource.outlineFontTexture!!, actor.color.mul(outlineColor!!, tempColor), text, actor.x, actor.y, modelMatrix)
        }
        draw(renderer, fontResource.fontTexture, actor.color.mul(color, tempColor), text, actor.x, actor.y, modelMatrix)
    }

    private fun draw(renderer: Renderer, fontTexture: FontTexture, color: Color, text: CharSequence, x: Double, y: Double, modelMatrix: Matrix4f? = null) {

        val dy = when (valignment) {
            TextVAlignment.TOP -> 0.0
            TextVAlignment.CENTER -> height(text) / 2
            TextVAlignment.BASELINE -> height(text) - fontTexture.descent
            TextVAlignment.BOTTOM -> height(text)
        }
        var lineY = y + dy

        text.split('\n').forEach { line ->

            val dx = when (halignment) {
                TextHAlignment.LEFT -> 0.0
                TextHAlignment.CENTER -> fontTexture.width(line) / 2
                TextHAlignment.RIGHT -> fontTexture.width(line)
            }

            fontTexture.draw(renderer, line, x - dx, lineY, color, modelMatrix)
            lineY -= fontTexture.lineHeight
        }

    }

    fun copy(): TextStyle = TextStyle(fontResource, halignment, valignment, color, outlineColor)
}

enum class TextHAlignment { LEFT, CENTER, RIGHT }
enum class TextVAlignment { BOTTOM, BASELINE, CENTER, TOP }
