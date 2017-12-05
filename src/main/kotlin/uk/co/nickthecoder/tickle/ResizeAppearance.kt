package uk.co.nickthecoder.tickle

import org.joml.Vector2d

/**
 * The base class for NinePatchAppearance and TiledAppearance which can both be given an arbitrary size without scaling.
 */
abstract class ResizeAppearance(actor: Actor) : AbstractAppearance(actor) {

    protected var height: Double = 0.0

    protected var width: Double = 0.0

    /**
     * The alignment, where x,y are both in the range 0..1
     * (0,0) means the Actor's position is at the bottom left of the NinePatch.
     * (1,1) means the Actor's position is at the top right of the NinePatch.
     */
    val alignment = Vector2d(0.5, 0.5)

    override fun height() = height

    override fun width() = width


    override fun offsetX() = width * alignment.x

    override fun offsetY() = height * alignment.y

    override fun touching(point: Vector2d) = pixelTouching(point)

    override fun resize(width: Double, height: Double) {
        this.width = width
        this.height = height
        actor.scaleXY = 1.0
    }

}
