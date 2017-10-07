package uk.co.nickthecoder.tickle.stage

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.util.Heading
import uk.co.nickthecoder.tickle.util.Recti


abstract class AbstractStageView
    : StageView {

    override var rect = Recti(0, 0, 10, 10)
        set(v) {
            if (field != v) {
                field = v
                projectionDirty = true
            }
        }

    override lateinit var stage: Stage

    override var centerX = 0f
        set(v) {
            if (field != v) {
                field = v
                projectionDirty = true
            }
        }

    override var centerY = 0f
        set(v) {
            if (field != v) {
                field = v
                projectionDirty = true
            }
        }

    val direction = object : Heading() {
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
    private val cachedWindowToWorld = Matrix4f()

    val projection: Matrix4f
        get() {
            if (projectionDirty) {
                recalculateMatrices()
            }
            return cachedProjection
        }

    val windowToWorld: Matrix4f
        get() {
            if (projectionDirty) {
                recalculateMatrices()
            }
            return cachedWindowToWorld
        }

    private fun recalculateMatrices() {
        val w = rect.width
        val h = rect.height

        cachedProjection.identity()
        cachedWindowToWorld.identity()

        cachedProjection.ortho2D(
                centerX - w / 2, centerX + w / 2,
                centerY - h / 2, centerY + h / 2)


        if (direction.degrees != 0.0) {
            cachedProjection.translate(centerX, centerY, 0f).rotateZ(direction.radians.toFloat()).translate(-centerX, -centerY, 0f)
        }

        cachedWindowToWorld
                .translate(centerX, centerY, 0f)
                .rotateZ(-direction.radians.toFloat())
                .translate(-rect.left - rect.width.toFloat() / 2f, -rect.bottom - rect.height.toFloat() / 2f, 0f)

        projectionDirty = false
    }

    override fun draw(renderer: Renderer) {
        GL11.glViewport(rect.left, rect.bottom, rect.width, rect.height)
        renderer.changeProjection(projection)
    }

    private val dummyVector2F = Vector2f()

    private val mousePosition4f = Vector4f()

    private val mousePosition2f = Vector2f()

    override fun mousePosition(): Vector2f {
        Window.current?.let { window ->

            val pos = window.mousePosition()
            mousePosition4f.x = pos.x
            mousePosition4f.y = window.height - pos.y
            mousePosition4f.z = 1f
            mousePosition4f.w = 1f

            windowToWorld.transform(mousePosition4f)
            mousePosition2f.x = mousePosition4f.x
            mousePosition2f.y = mousePosition4f.y
            return mousePosition2f
        }
        return dummyVector2F
    }

}
