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
package uk.co.nickthecoder.tickle.stage

import org.joml.Matrix4f
import org.joml.Vector2d
import org.lwjgl.opengl.GL11
import uk.co.nickthecoder.tickle.ActorDetails
import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.events.MouseButtonListener
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseListener
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Recti

// Note, this is marked as abstract, so that the Editor will NOT show it in the list of possible StageViews,
// as it requires a zero argument constructor.

abstract class AbstractStageView(override val comparator: Comparator<ActorDetails>)
    : StageView, MouseListener {

    override val roleComparator: Comparator<Role> = Comparator { o1, o2 ->
        comparator.compare(o1.actor, o2.actor)
    }

    override var zOrder = 0

    override var rect = Recti(0, 0, 10, 10)
        set(v) {
            if (field != v) {
                field = v
                // The default is for a view to have (0,0) a the bottom left of its viewport.
                centerX = (rect.width) / 2.0
                centerY = (rect.height) / 2.0
                projectionDirty = true
            }
        }

    override lateinit var stage: Stage

    override var centerX = 0.0
        set(v) {
            if (field != v) {
                field = v
                projectionDirty = true
            }
        }

    override var centerY = 0.0
        set(v) {
            if (field != v) {
                field = v
                projectionDirty = true
            }
        }

    val direction = object : Angle() {
        override var radians = 0.0
            set(v) {
                if (field != v) {
                    field = v
                    projectionDirty = true
                }
            }
    }

    private var projectionDirty: Boolean = true
    private val cachedProjection = Matrix4f()

    val projection: Matrix4f
        get() {
            if (projectionDirty) {
                recalculateMatrices()
            }
            return cachedProjection
        }

    override var handleMouseButtons: Boolean = true

    var mouseCapturedBy: MouseButtonListener? = null

    override fun screenToView(screen: Vector2d, into: Vector2d) {
        val fromCenterX = screen.x - (rect.left + rect.right) / 2.0
        val fromCenterY = (Window.instance?.height ?: Resources.instance.gameInfo.height) - screen.y - (rect.top + rect.bottom) / 2.0

        if (direction.radians == 0.0) {
            into.set(centerX + fromCenterX, centerY + fromCenterY)
        } else {
            val sin = Math.sin(direction.radians)
            val cos = Math.cos(direction.radians)
            into.set(centerX + fromCenterX * cos - fromCenterY * sin, fromCenterY * sin + fromCenterX * cos)
        }
    }

    private fun recalculateMatrices() {
        val w = rect.width
        val h = rect.height

        cachedProjection.identity()
        //cachedScreenToView.identity()

        cachedProjection.ortho2D(
                (centerX - w / 2).toFloat(), (centerX + w / 2).toFloat(),
                (centerY - h / 2).toFloat(), (centerY + h / 2).toFloat())


        if (direction.degrees != 0.0) {
            cachedProjection
                    .translate(centerX.toFloat(), centerY.toFloat(), 0f)
                    .rotateZ(direction.radians.toFloat())
                    .translate(-centerX.toFloat(), -centerY.toFloat(), 0f)
        }

        //cachedScreenToView
        //        .translate(centerX.toFloat(), centerY.toFloat(), 0f)
        //        .rotateZ(-direction.radians.toFloat())
        //        .translate(-rect.left - rect.width.toFloat() / 2f, -rect.bottom - rect.height.toFloat() / 2f, 0f)

        projectionDirty = false
    }

    override fun draw(renderer: Renderer) {
        GL11.glViewport(rect.left, rect.bottom, rect.width, rect.height)
        renderer.changeProjection(projection)

        stage.actors.sortedWith(comparator).forEach { actor ->
            actor.appearance.draw(renderer)
        }
    }

    override fun onMouseButton(event: MouseEvent) {

        if (handleMouseButtons) {

            screenToView(event.screenPosition, event.viewPosition)

            mouseCapturedBy?.let {
                event.captured = true
                it.onMouseButton(event)
                if (event.isConsumed()) {
                    if (!event.captured) {
                        mouseCapturedBy = null
                    }
                    return
                }
                return
            }

            if (rect.contains(event.screenPosition)) {
                findActorsAt(event.viewPosition).forEach { actor ->
                    val role = actor.role
                    if (role is MouseButtonListener) {
                        role.onMouseButton(event)
                        if (event.isConsumed()) {
                            if (event.captured) {
                                mouseCapturedBy = role
                            } else {
                                mouseCapturedBy = null
                            }
                            return
                        }

                    }
                }
            }
        }

    }

    override fun onMouseMove(event: MouseEvent) {
        screenToView(event.screenPosition, event.viewPosition)
        if (mouseCapturedBy is MouseListener) {
            (mouseCapturedBy as MouseListener).onMouseMove(event)
        }
    }

    override fun tick() {}
}
