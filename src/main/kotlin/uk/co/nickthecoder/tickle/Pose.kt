package uk.co.nickthecoder.tickle

import org.joml.Matrix4f
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Rectd
import uk.co.nickthecoder.tickle.util.YDownRect

class Pose(
        val texture: Texture,
        rect: YDownRect = YDownRect(0, 0, texture.width, texture.height)) {

    var rect: YDownRect = rect
        set(v) {
            field = v
            updateRectd()
        }

    var offsetX: Double = 0.0
    var offsetY: Double = 0.0

    /**
     * The natural direction. i.e. if the Actor moved "forward", which mathematical angle would that be?
     * For the default value of 0, the image is pointing to the right.
     */
    val direction = Angle()


    private val rectd = Rectd(0.0, 0.0, 1.0, 1.0)

    init {
        updateRectd()
    }

    fun updateRectd() {
        rectd.left = rect.left.toDouble() / texture.width
        rectd.bottom = 1 - (rect.bottom.toDouble() / texture.height)
        rectd.right = rect.right.toDouble() / texture.width
        rectd.top = 1 - (rect.top.toDouble() / texture.height)
    }

    fun draw(renderer: Renderer, x: Double, y: Double, color: Color = Color.WHITE, modelMatrix: Matrix4f? = null) {
        val left = x - offsetX
        val bottom = y - offsetY
        renderer.drawTexture(
                texture,
                left, bottom, left + rect.width, bottom + rect.height,
                rectd,
                color,
                modelMatrix)
    }

    fun draw(renderer: Renderer, actor: Actor) {
        val left = actor.x - offsetX
        val bottom = actor.y - offsetY

        if (actor.isSimpleImage()) {
            renderer.drawTexture(
                    texture,
                    left, bottom, left + rect.width, bottom + rect.height,
                    rectd,
                    color = actor.color)

        } else {
            renderer.drawTexture(
                    texture,
                    left, bottom, left + rect.width, bottom + rect.height,
                    rectd,
                    color = actor.color,
                    modelMatrix = actor.getModelMatrix())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Pose) {
            return false
        }
        return (rect == other.rect) && rectd == other.rectd && texture == other.texture && direction.radians == other.direction.radians
    }

    override fun toString(): String {
        return "Pose rect=$rect offset=$offsetX,$offsetY direction=$direction.degrees rectd=$rectd"
    }
}
