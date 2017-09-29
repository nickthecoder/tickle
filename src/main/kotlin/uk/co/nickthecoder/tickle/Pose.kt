package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Rectf
import uk.co.nickthecoder.tickle.util.Recti

class Pose(
        val name: String,
        val texture: Texture,
        rect: Recti = Recti(0, texture.height, texture.width, 0)) {

    var rect: Recti = rect
        set(v) {
            updateRectf()
        }

    var offsetX: Float = 0f
    var offsetY: Float = 0f

    private val rectf = Rectf(0f, 0f, 1f, 1f)

    init {
        updateRectf()
    }

    fun updateRectf() {
        rectf.left = rect.left.toFloat() / texture.width
        rectf.bottom = 1 - (rect.bottom.toFloat() / texture.height)
        rectf.right = rect.right.toFloat() / texture.width
        rectf.top = 1 - (rect.top.toFloat() / texture.height)

        println("Pose $name rectangles $rect and $rectf")
    }

    fun draw(renderer: Renderer, actor: Actor) {
        val left = actor.x - offsetX
        val bottom = actor.y - offsetY

        if (actor.isSimpleImage()) {
            renderer.drawTexture(
                    texture,
                    left, bottom, left + rect.width, bottom + rect.height,
                    rectf,
                    color = actor.color)
        } else {
            renderer.drawTexture(
                    texture,
                    left, bottom, left + rect.width, bottom + rect.height,
                    rectf, color = actor.color,
                    modelMatrix = actor.getModelMatrix())
        }
    }

}
