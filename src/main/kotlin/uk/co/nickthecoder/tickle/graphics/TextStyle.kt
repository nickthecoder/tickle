package uk.co.nickthecoder.tickle.graphics

import uk.co.nickthecoder.tickle.FontResource

class TextStyle(
        var fontResource: FontResource,
        var halignment: HAlignment,
        var valignment: VAlignment,
        var color: Color) {

    fun offsetX(text: CharSequence): Double {
        return when (halignment) {
            HAlignment.LEFT -> 0.0
            HAlignment.RIGHT -> fontResource.fontTexture.width(text)
            HAlignment.CENTER -> fontResource.fontTexture.width(text) / 2.0
        }
    }

    fun offsetY(text: CharSequence): Double {
        return when (valignment) {
            VAlignment.TOP -> fontResource.fontTexture.height(text)
            VAlignment.BOTTOM -> 0.0
            VAlignment.CENTER -> fontResource.fontTexture.height(text) / 2.0
            VAlignment.BASELINE -> 0.0
        }
    }

    fun width(text: CharSequence) = fontResource.fontTexture.width(text)

    fun height(text: CharSequence) = fontResource.fontTexture.height(text)

    fun draw(renderer: Renderer, text: CharSequence, x: Double, y: Double, color: Color = Color.WHITE) {

        val dy = when (valignment) {
            VAlignment.TOP -> 0.0
            VAlignment.CENTER -> height(text) / 2
            VAlignment.BASELINE -> height(text) - fontResource.fontTexture.descent
            VAlignment.BOTTOM -> height(text)
        }
        var lineY = y + dy

        text.split('\n').forEach { line ->

            val dx = when (halignment) {
                HAlignment.LEFT -> 0.0
                HAlignment.CENTER -> fontResource.fontTexture.width(line) / 2
                HAlignment.RIGHT -> fontResource.fontTexture.width(line)
            }

            fontResource.fontTexture.draw(renderer, line, x - dx, lineY, color)
            lineY -= fontResource.fontTexture.lineHeight
        }

    }
}

enum class HAlignment { LEFT, CENTER, RIGHT }
enum class VAlignment { BOTTOM, BASELINE, CENTER, TOP }
