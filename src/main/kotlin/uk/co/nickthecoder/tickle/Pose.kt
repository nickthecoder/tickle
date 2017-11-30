package uk.co.nickthecoder.tickle

import org.joml.Matrix4f
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.*

class Pose(
        val texture: Texture,
        rect: YDownRect = YDownRect(0, 0, texture.width, texture.height))

    : Copyable<Pose>, Deletable, Renamable {

    var rect: YDownRect = rect
        set(v) {
            field = v
            updateRectd()
        }

    var offsetX: Double = 0.0
    var offsetY: Double = 0.0

    /**
     * Points other than the offsetX,Y, which can be snapped to.
     */
    var snapPoints = mutableListOf<Vector2d>()

    /**
     * The natural direction. i.e. if the Actor moved "forward", which mathematical angle would that be?
     * For the default value of 0, the image is pointing to the right.
     */
    val direction = Angle()


    internal val rectd = Rectd(0.0, 0.0, 1.0, 1.0)

    init {
        updateRectd()
    }

    fun updateRectd() {
        rectd.left = rect.left.toDouble() / texture.width
        rectd.bottom = 1 - (rect.bottom.toDouble() / texture.height)
        rectd.right = rect.right.toDouble() / texture.width
        rectd.top = 1 - (rect.top.toDouble() / texture.height)
    }

    private val WHITE = Color.white()

    fun draw(renderer: Renderer, x: Double, y: Double, color: Color = WHITE, modelMatrix: Matrix4f? = null) {
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
                    modelMatrix = actor.calculateModelMatrix())
        }
    }


    override fun usedBy(): Any? {
        return Resources.instance.costumes.items().values.firstOrNull { it.uses(this) }
    }

    override fun delete() {
        Resources.instance.poses.remove(this)
    }

    override fun rename(newName: String) {
        Resources.instance.poses.rename(this, newName)
    }


    override fun copy(): Pose {
        val copy = Pose(texture, YDownRect(rect.left, rect.top, rect.right, rect.bottom))
        copy.offsetX = offsetX
        copy.offsetY = offsetY
        copy.direction.radians = direction.radians
        return copy
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
