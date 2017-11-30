package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import org.joml.Vector4f
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.util.Rectd
import uk.co.nickthecoder.tickle.util.rotate

interface Appearance {

    val directionRadians: Double

    /**
     * The natural direction. i.e. if the Actor moved "forward", which mathematical angle would that be?
     * For the default value of 0, the image is pointing to the right.
     */
    fun draw(renderer: Renderer)

    /**
     * The width of the pose or the text (before scaling/rotating)
     */
    fun width(): Double

    /**
     * The height of the pose or the text (before scaling/rotating)
     */
    fun height(): Double

    /**
     * The offset position of the pose or the text (before scaling/rotating/flipping)
     */
    fun offsetX(): Double

    /**
     * The offset position of the pose or the text (before scaling/rotating/flipping)
     */
    fun offsetY(): Double

    /**
     * The bounding rectangle of the actor after scaling, rotating, flipping etc.
     * Note, that this is a rectangle aligned with the x,y axis, and therefore when a long, thin object
     * is rotated, the rectangle can be substantially larger that the object.
     */
    fun worldRect(): Rectd

    /**
     * Is the point within the actor's rectangular region? Note, this uses a rectangle not aligned
     * with the x/y axis, and is therefore still useful when the actor is rotated.
     */
    fun contains(point: Vector2d): Boolean

    fun touching(point: Vector2d): Boolean
}

private val INVISIBLE_RECT = Rectd(0.0, 0.0, 0.0, 0.0)

class InvisibleAppearance : Appearance {

    override val directionRadians = 0.0

    override fun draw(renderer: Renderer) {
        // Do nothing
    }

    override fun width(): Double = 0.0

    override fun height(): Double = 0.0

    override fun offsetX(): Double = 0.0

    override fun offsetY(): Double = 0.0

    override fun worldRect() = INVISIBLE_RECT

    override fun contains(point: Vector2d): Boolean = false

    override fun touching(point: Vector2d): Boolean = false

    override fun toString() = "InvisibleAppearance"

}

abstract class AbstractAppearance(val actor: Actor) : Appearance {

    private val worldRect = Rectd()

    override fun worldRect(): Rectd {
        // TODO. It would be good to cache this info similar to getModelMatrix.
        worldRect.left = actor.x - offsetX()
        worldRect.bottom = actor.y - offsetY()
        worldRect.right = worldRect.left + width()
        worldRect.top = worldRect.bottom + height()
        if (!actor.isSimpleImage()) {
            // Rotates/scale/flip each corner of the rectangle...
            val a = Vector4f(worldRect.left.toFloat(), worldRect.top.toFloat(), 0f, 1f)
            val b = Vector4f(worldRect.left.toFloat(), worldRect.bottom.toFloat(), 0f, 1f)
            val c = Vector4f(worldRect.right.toFloat(), worldRect.top.toFloat(), 0f, 1f)
            val d = Vector4f(worldRect.right.toFloat(), worldRect.bottom.toFloat(), 0f, 1f)
            val matrix = actor.calculateModelMatrix()
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

    override fun contains(point: Vector2d): Boolean {
        tempVector.set(point)
        tempVector.sub(actor.position)

        if (actor.direction.radians != 0.0) {
            tempVector.rotate(-actor.direction.radians)
        }

        tempVector.x /= actor.scale.x
        tempVector.y /= actor.scale.y

        val offsetX = offsetX()
        val offsetY = offsetY()

        return tempVector.x >= -offsetX && tempVector.x < width() - offsetX && tempVector.y > -offsetY && tempVector.y < height() - offsetY
    }

}

class PoseAppearance(actor: Actor, var pose: Pose)

    : AbstractAppearance(actor) {

    override val directionRadians
        get() = pose.direction.radians

    override fun draw(renderer: Renderer) {
        pose.draw(renderer, actor)
    }

    override fun width(): Double = pose.rect.width.toDouble()

    override fun height(): Double = pose.rect.height.toDouble()

    override fun offsetX() = pose.offsetX

    override fun offsetY() = pose.offsetY


    override fun contains(point: Vector2d): Boolean {
        tempVector.set(point)

        tempVector.sub(actor.position)

        if (actor.direction.radians != 0.0) {
            tempVector.rotate(-actor.direction.radians + pose.direction.radians)
        }
        tempVector.x /= actor.scale.x
        tempVector.y /= actor.scale.y

        val offsetX = offsetX()
        val offsetY = offsetY()

        return tempVector.x >= -offsetX && tempVector.x < width() - offsetX && tempVector.y > -offsetY && tempVector.y < height() - offsetY
    }

    // TODO Implement correctly
    override fun touching(point: Vector2d): Boolean = contains(point)

    override fun toString() = "PoseAppearance pose=$pose"
}

class TextAppearance(actor: Actor, var text: String, val textStyle: TextStyle)

    : AbstractAppearance(actor) {

    override val directionRadians = 0.0

    override fun draw(renderer: Renderer) {
        textStyle.draw(renderer, text, actor)
    }

    override fun width(): Double = textStyle.width(text)
    override fun height(): Double = textStyle.height(text)

    override fun offsetX(): Double = textStyle.offsetX(text)
    override fun offsetY(): Double = textStyle.height(text) - textStyle.offsetY(text)

    override fun touching(point: Vector2d): Boolean = contains(point)

    override fun toString() = "TextAppearance '$text'"
}

private val tempVector = Vector2d()
