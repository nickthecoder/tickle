package uk.co.nickthecoder.tickle.graphics

import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.resources.FontResource

class TextStyle(
        var fontResource: FontResource,
        var halignment: HAlignment,
        var valignment: VAlignment,
        var color: Color,
        var outlineColor: Color? = null) {

    fun offsetX(text: CharSequence): Double {
        return when (halignment) {
            HAlignment.LEFT -> 0.0
            HAlignment.RIGHT -> width(text)
            HAlignment.CENTER -> width(text) / 2.0
        }
    }

    fun offsetY(text: CharSequence): Double {
        return when (valignment) {
            VAlignment.TOP -> 0.0
            VAlignment.CENTER -> height(text) / 2.0
            VAlignment.BASELINE -> height(text) - fontResource.fontTexture.descent
            VAlignment.BOTTOM -> height(text)
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
            VAlignment.TOP -> 0.0
            VAlignment.CENTER -> height(text) / 2
            VAlignment.BASELINE -> height(text) - fontTexture.descent
            VAlignment.BOTTOM -> height(text)
        }
        var lineY = y + dy

        text.split('\n').forEach { line ->

            val dx = when (halignment) {
                HAlignment.LEFT -> 0.0
                HAlignment.CENTER -> fontTexture.width(line) / 2
                HAlignment.RIGHT -> fontTexture.width(line)
            }

            fontTexture.draw(renderer, line, x - dx, lineY, color, modelMatrix)
            lineY -= fontTexture.lineHeight
        }

    }
}

enum class HAlignment { LEFT, CENTER, RIGHT }
enum class VAlignment { BOTTOM, BASELINE, CENTER, TOP }
