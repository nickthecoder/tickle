package uk.co.nickthecoder.tickle

import org.joml.Vector4f
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Rectd

interface Appearance {

    val directionRadians: Double

    /**
     * The natural direction. i.e. if the Actor moved "forward", which mathematical angle would that be?
     * For the default value of 0, the image is pointing to the right.
     */
    fun draw(renderer: Renderer)

    fun width(): Double

    fun height(): Double

    fun worldRect(): Rectd
}

private val INVISIBLE_RECT = Rectd(0.0, 0.0, 0.0, 0.0)

class InvisibleAppearance : Appearance {

    override val directionRadians = 0.0

    override fun draw(renderer: Renderer) {
        // Do nothing
    }

    override fun width(): Double = 0.0

    override fun height(): Double = 0.0

    override fun worldRect() = INVISIBLE_RECT

    override fun toString() = "InvisibleAppearance"

}

class PoseAppearance(val actor: Actor, var pose: Pose) : Appearance {

    private val worldRect = Rectd()

    override val directionRadians
        get() = pose.direction.radians

    override fun draw(renderer: Renderer) {
        pose.draw(renderer, actor)
    }

    override fun width(): Double = pose.rect.width.toDouble() * actor.scale
    override fun height(): Double = pose.rect.height.toDouble() * actor.scale

    override fun worldRect(): Rectd {
        // TODO. It would be good to cache this info similar to getModelMatrix.
        worldRect.left = actor.x - pose.offsetX
        worldRect.bottom = actor.y - pose.offsetY
        worldRect.right = worldRect.left + pose.rect.width
        worldRect.top = worldRect.bottom + pose.rect.height
        if (!actor.isSimpleImage()) {
            // Rotates/scale/flip each corner of the rectangle...
            val a = Vector4f(worldRect.left.toFloat(), worldRect.top.toFloat(), 0f, 1f)
            val b = Vector4f(worldRect.left.toFloat(), worldRect.bottom.toFloat(), 0f, 1f)
            val c = Vector4f(worldRect.right.toFloat(), worldRect.top.toFloat(), 0f, 1f)
            val d = Vector4f(worldRect.right.toFloat(), worldRect.bottom.toFloat(), 0f, 1f)
            val matrix = actor.getModelMatrix()
            a.mul(matrix)
            b.mul(matrix)
            c.mul(matrix)
            d.mul(matrix)
            // Find the rectangle containing the transformed corners.
            worldRect.left = Math.min(Math.min(a.x, b.x), Math.min(c.x, d.x)).toDouble()
            worldRect.bottom = Math.min(Math.min(a.y, b.y), Math.min(c.y, d.y)).toDouble()
            worldRect.right = Math.max(Math.max(a.x, b.x), Math.max(c.x, d.x)).toDouble()
            worldRect.top = Math.max(Math.max(a.y, b.y), Math.max(c.y, d.y)).toDouble()
        }
        return worldRect
    }

    override fun toString() = "PoseAppearance pose=$pose"
}

class TextAppearance(val actor: Actor, var text: String, val textStyle: TextStyle) : Appearance {

    override val directionRadians = 0.0

    override fun draw(renderer: Renderer) {
        textStyle.draw(renderer, text, actor)
    }

    override fun width(): Double = textStyle.width(text) * actor.scale
    override fun height(): Double = textStyle.height(text) * actor.scale

    // TODO Inplement correctly
    override fun worldRect() = INVISIBLE_RECT

    override fun toString() = "TextAppearance '$text'"
}
