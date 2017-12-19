package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.physics.TickleWorld
import uk.co.nickthecoder.tickle.physics.offset
import uk.co.nickthecoder.tickle.physics.scale

/**
 * The base class for NinePatchAppearance and TiledAppearance which can both be given an arbitrary size without scaling.
 */
abstract class ResizeAppearance(actor: Actor) : AbstractAppearance(actor) {

    val size = Vector2d(0.0, 0.0)

    /**
     * The alignment, where x,y are both in the range 0..1
     * (0,0) means the Actor's position is at the bottom left of the NinePatch.
     * (1,1) means the Actor's position is at the top right of the NinePatch.
     */
    val alignment = Vector2d(0.5, 0.5)

    internal val oldSize = Vector2d(0.0, 0.0)

    protected val oldAlignment = Vector2d(0.5, 0.5)

    private var bodyDirty = false


    override fun height() = size.y

    override fun width() = size.x


    override fun offsetX() = size.x * alignment.x

    override fun offsetY() = size.y * alignment.y

    override fun touching(point: Vector2d) = pixelTouching(point)

    override fun resize(width: Double, height: Double) {
        size.x = width
        size.y = height
    }

    override fun updateBody() {
        if (oldSize != size) {
            actor.body?.scale((size.x / oldSize.x).toFloat(), (size.y / oldSize.y).toFloat())

            oldSize.set(size)
        }
        if (oldAlignment != alignment) {
            actor.body?.let { body ->
                val world = body.world as TickleWorld
                body.offset(
                        world.pixelsToWorld((oldAlignment.x - alignment.x) * width()),
                        world.pixelsToWorld((oldAlignment.y - alignment.y) * height()))
            }
            oldAlignment.set(alignment)
        }
    }

}
