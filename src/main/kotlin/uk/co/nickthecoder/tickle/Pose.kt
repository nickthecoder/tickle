package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Rectf
import uk.co.nickthecoder.tickle.util.YDownRect

class Pose(
        val texture: Texture,
        rect: YDownRect = YDownRect(0, 0, texture.width, texture.height)) {

    var rect: YDownRect = rect
        set(v) {
            field = v
            updateRectf()
        }

    var offsetX: Float = 0f
    var offsetY: Float = 0f

    /**
     * The natural direction. i.e. if the Actor moved "forward", which mathematical angle would that be?
     * For the default value of 0, the image is pointing to the right.
     */
    var directionRadians: Double = 0.0

    var directionDegrees: Double
        get() = Math.toDegrees(directionRadians)
        set(v) {
            directionRadians = Math.toRadians(v)
        }

    private val rectf = Rectf(0f, 0f, 1f, 1f)

    init {
        updateRectf()
    }

    fun updateRectf() {
        rectf.left = rect.left.toFloat() / texture.width
        rectf.bottom = 1 - (rect.bottom.toFloat() / texture.height)
        rectf.right = rect.right.toFloat() / texture.width
        rectf.top = 1 - (rect.top.toFloat() / texture.height)
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

    override fun equals(other: Any?): Boolean {
        if (other !is Pose) {
            return false
        }
        return (rect == other.rect) && rectf == other.rectf && texture == other.texture && directionRadians == other.directionRadians
    }

    override fun toString(): String {
        return "Pose rect=$rect offset=$offsetX,$offsetY direction=$directionDegrees rectf=$rectf"
    }
}
