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

import org.joml.Matrix4f
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.util.radians
import uk.co.nickthecoder.tickle.util.string

data class NinePatch(var pose: Pose, var left: Int, var bottom: Int, var right: Int, var top: Int)

/**
 * Draws an [Actor] using a nine-patch, which allows the actor scaled to any size, and the nine patch
 * will only scale the inner parts. The corners will be draw 1:1.
 *
 * Note. when using nine patches, the Actor's scale is ignored. Use [Actor.resize] to resize.
 * [Actor.flipX] and [Actor.flipY] are also currently ignored.
 */
class NinePatchAppearance(actor: Actor, val ninePatch: NinePatch) : ResizeAppearance(actor) {

    override val directionRadians: Double
        get() = ninePatch.pose.direction.radians

    /**
     * A 3x3 array of Poses
     */
    private val pieces = Array<Array<Pose>>(3) {
        Array<Pose>(3) {
            val piecePose = ninePatch.pose.copy()
            piecePose.offsetX = 0.0
            piecePose.offsetY = 0.0
            piecePose

        }
    }

    private val tempVector = Vector2d()

    init {
        val w = ninePatch.pose.rect.width
        val h = ninePatch.pose.rect.height
        val widths = listOf(ninePatch.left, w - ninePatch.left - ninePatch.right, ninePatch.right)
        val heights = listOf(ninePatch.bottom, h - ninePatch.bottom - ninePatch.top, ninePatch.top)

        // Relative to the parent pose.
        val lefts = listOf(0, ninePatch.left, w - ninePatch.right)
        val tops = listOf(h - ninePatch.bottom, ninePatch.top, 0)

        for (x in 0..2) {
            for (y in 0..2) {
                val piecePose = pieces[x][y]

                piecePose.rect.left = ninePatch.pose.rect.left + lefts[x]
                piecePose.rect.right = piecePose.rect.left + widths[x]

                piecePose.rect.top = ninePatch.pose.rect.top + tops[y]
                piecePose.rect.bottom = piecePose.rect.top + heights[y]

                piecePose.updateRectd()
            }
        }
        size.x = ninePatch.pose.rect.width.toDouble()
        size.y = ninePatch.pose.rect.height.toDouble()
        oldSize.set(size)

        sizeAlignment.x = ninePatch.pose.offsetX / ninePatch.pose.rect.width
        sizeAlignment.y = ninePatch.pose.offsetY / ninePatch.pose.rect.height
        oldAlignment.set(sizeAlignment)
    }

    override fun draw(renderer: Renderer) {

        val widths = listOf(ninePatch.left.toDouble(), width() - ninePatch.left - ninePatch.right, ninePatch.right.toDouble())
        val heights = listOf(ninePatch.bottom.toDouble(), height() - ninePatch.top - ninePatch.bottom, ninePatch.top.toDouble())

        val lefts = listOf(0.0, ninePatch.left.toDouble(), (width() - ninePatch.right))
        val bottoms = listOf(0.0, ninePatch.bottom.toDouble(), (height() - ninePatch.top))

        //println("Drawing nine patch $width , $height widths=$widths heights = $heights")

        val simple = actor.isSimpleImage()
        val modelMatrix: Matrix4f?
        if (!simple) {
            modelMatrix = actor.calculateModelMatrix()
        } else {
            modelMatrix = null
        }

        for (y in 0..2) {
            for (x in 0..2) {
                val piecePose = pieces[x][y]

                if (piecePose.rect.width > 0 && piecePose.rect.height > 0) {
                    val x0 = actor.x - offsetX() + lefts[x]
                    val y0 = actor.y - offsetY() + bottoms[y]
                    if (simple) {
                        //println("Drawing simple piece $x,$y width=${piecePose.rect.width} height=${piecePose.rect.height} at ( $x0, $y0, ${x0 + widths[x]}, ${y0 + heights[y]} ), ${piecePose.rectd}")
                        renderer.drawTexture(ninePatch.pose.texture, x0, y0, x0 + widths[x], y0 + heights[y], piecePose.rectd, actor.color)
                    } else {
                        renderer.drawTexture(ninePatch.pose.texture, x0, y0, x0 + widths[x], y0 + heights[y], piecePose.rectd, actor.color, modelMatrix)
                    }
                }
            }
        }
    }

    /**
     * Use a NinePatch as a line, from the Actor's position to an arbitrary point.
     * The Actor's direction is calculated from the angle between these to points. The nine patch's x size is resized to
     * the magnitude of the line, and the height remains the same.
     * Therefore, the NinePatch should be drawn with the line pointing along the X axis.
     */
    fun lineTo(x: Double, y: Double) {
        tempVector.set(x - actor.position.x, y - actor.position.y)
        actor.direction.radians = tempVector.radians()
        size.x = tempVector.length()
    }

    /**
     * Use a NinePatch as a line, from the Actor's position to an arbitrary point.
     * The Actor's direction is calculated from the angle between these to points. The nine patch's x size is resized to
     * the magnitude of the line, and the height remains the same.
     * Therefore, the NinePatch should be drawn with the line pointing along the X axis.
     */
    fun lineTo(point: Vector2d) {
        point.sub(actor.position, tempVector)
        actor.direction.radians = tempVector.radians()
        size.x = tempVector.length()
    }

    override fun toString() = "NinePatchAppearance pose=${ninePatch.pose} size=${size.string()} margins : ${ninePatch.left}, ${ninePatch.bottom}, ${ninePatch.right}, ${ninePatch.top}"

}
