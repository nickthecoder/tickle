/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.physics.offset
import uk.co.nickthecoder.tickle.physics.scale

/**
 * The base class for NinePatchAppearance and TiledAppearance which can both be given an arbitrary size without scaling.
 */
abstract class ResizeAppearance(actor: Actor) : AbstractAppearance(actor) {

    val size = Vector2d(1.0, 1.0)

    /**
     * The alignment, where x,y are both in the range 0..1
     * (0,0) means the Actor's position is at the bottom left of the NinePatch.
     * (1,1) means the Actor's position is at the top right of the NinePatch.
     */
    val sizeAlignment = Vector2d(0.5, 0.5)

    internal val oldSize = Vector2d(1.0, 1.0)

    protected val oldAlignment = Vector2d(0.5, 0.5)


    override fun height() = size.y

    override fun width() = size.x


    override fun offsetX() = size.x * sizeAlignment.x

    override fun offsetY() = size.y * sizeAlignment.y

    override fun touching(point: Vector2d) = pixelTouching(point)

    override fun resize(width: Double, height: Double) {
        size.x = width
        size.y = height
    }

    override fun updateBody() {
        if (oldSize != size) {
            actor.body?.jBox2DBody?.scale((size.x / oldSize.x).toFloat(), (size.y / oldSize.y).toFloat())

            oldSize.set(size)
        }
        if (oldAlignment != sizeAlignment) {
            actor.body?.let { body ->
                val world = body.tickleWorld
                body.jBox2DBody.offset(
                        world.pixelsToWorld((oldAlignment.x - sizeAlignment.x) * width()),
                        world.pixelsToWorld((oldAlignment.y - sizeAlignment.y) * height()))
            }
            oldAlignment.set(sizeAlignment)
        }
    }

}
