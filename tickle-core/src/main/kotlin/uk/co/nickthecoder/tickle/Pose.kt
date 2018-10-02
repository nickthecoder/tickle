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
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.*

class Pose(
        val texture: Texture,
        rect: YDownRect = YDownRect(0, 0, texture.width, texture.height))

    : Copyable<Pose>, Deletable, Renamable, Dependable {

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

    /**
     * If the pose is tiled, then the actor will use a TiledAppearance, and it can be resized without scaling.
     */
    var tiled = false

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


    // Dependency

    // Deletable
    override fun dependables(): List<Costume> {
        return Resources.instance.costumes.items().values.filter { it.dependsOn(this) }
    }

    override fun delete() {
        Resources.instance.poses.remove(this)
    }


    // Renamable
    override fun rename(newName: String) {
        Resources.instance.poses.rename(this, newName)
    }


    // Copyable
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
        return "Pose rect=$rect offset=($offsetX , $offsetY) direction=$direction rectd=$rectd"
    }
}
