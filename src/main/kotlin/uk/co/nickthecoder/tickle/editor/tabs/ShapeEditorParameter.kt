package uk.co.nickthecoder.tickle.editor.tabs

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import org.joml.Matrix3x2d
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.AbstractParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.util.AngleParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.editor.util.image
import uk.co.nickthecoder.tickle.physics.BoxDef
import uk.co.nickthecoder.tickle.physics.CircleDef
import uk.co.nickthecoder.tickle.physics.PolygonDef
import uk.co.nickthecoder.tickle.physics.ShapeDef

class ShapeEditorParameter(name: String, val pose: Pose, val fixtureParameter: CostumeTab.PhysicsTask.FixtureParameter)

    : AbstractParameter(name, label = "", description = "") {

    override fun errorMessage(): String? = null

    override fun copy() = ShapeEditorParameter(name, pose, fixtureParameter)

    override fun isStretchy(): Boolean = true

    private var field = ShapeEditorField(this)

    override fun createField(): ParameterField {
        field.build()
        return field
    }

    fun update(shapedDef: ShapeDef?) {
        field.update(shapedDef)
    }

}

class ShapeEditorTask(pose: Pose, val fixtureParameter: CostumeTab.PhysicsTask.FixtureParameter)
    : AbstractTask() {

    val shapeEditorP = ShapeEditorParameter("shapeEditor", pose, fixtureParameter)

    override val taskD = TaskDescription("editShape")
            .addParameters(shapeEditorP)

    override fun run() {
        Platform.runLater {

        }
    }
}

class ShapeEditorField(shapeEditorParameter: ShapeEditorParameter) : ParameterField(shapeEditorParameter) {

    val fixtureParameter = shapeEditorParameter.fixtureParameter
    val pose = shapeEditorParameter.pose
    val poseWidth = pose.rect.width
    val poseHeight = pose.rect.height

    val margin = 10.0
    val borderColor = Color(0.0, 0.0, 0.0, 0.3)
    val shapeColor = Color(1.0, 0.0, 0.0, 1.0)
    val handleColor = Color(0.0, 0.0, 1.0, 1.0)
    val currentHandleColor = Color(1.0, 1.0, 1.0, 1.0)

    val canvas = Canvas(poseWidth.toDouble() + margin * 2, poseHeight.toDouble() + margin * 2)

    val handles = mutableListOf<Handle>()

    init {
        //println("Creating SEP")
        with(canvas) {
            graphicsContext2D.transform(1.0, 0.0, 0.0, -1.0, 0.0, canvas.height)

            addEventHandler(MouseEvent.MOUSE_PRESSED) { onMousePressed(it) }
            addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
            addEventHandler(MouseEvent.MOUSE_DRAGGED) { onMouseDragged(it) }
            addEventHandler(MouseEvent.MOUSE_RELEASED) { dragging = false }
        }
    }

    override fun createControl(): Node {
        return canvas
    }

    var dirty = false
        set(v) {
            if (v && !field) {
                Platform.runLater {
                    redraw()
                }
            }
            field = v
        }

    var currentHandle: Handle? = null
        set(v) {
            if (field != v) {
                field = v
                dirty = true
            }
        }

    var dragging = false

    var currentShapedDef: ShapeDef? = null


    fun closestHandle(event: MouseEvent): Handle? {
        val offsetX = event.x - margin - pose.offsetX
        val offsetY = canvas.height - margin - event.y - pose.offsetY

        var result: Handle? = null
        var minDist = Double.MAX_VALUE
        handles.forEach { handle ->
            val position = handle.position()
            val dx = Math.abs(position.x - offsetX)
            val dy = Math.abs(position.y - offsetY)

            if (dx <= 6.0 && dy <= 6) {
                val dist = dx * dx + dy * dy
                if (dist < minDist) {
                    minDist = dist
                    result = handle
                }
            }
        }
        return result
    }

    fun onMousePressed(event: MouseEvent) {
        currentHandle = closestHandle(event)
        dragging = currentHandle != null
    }

    fun onMouseMoved(event: MouseEvent) {
        // println("onMouseMoved")
        currentHandle = closestHandle(event)
    }

    fun onMouseDragged(event: MouseEvent) {
        //println("onMouseDragged")

        val offsetX = event.x - margin - pose.offsetX
        val offsetY = canvas.height - margin - event.y - pose.offsetY
        currentHandle?.moveTo(offsetX, offsetY)
    }

    fun update(shapeDef: ShapeDef?) {

        //println("Update using : $shapeDef")
        currentShapedDef = shapeDef

        if (!dragging) {
            //println("Creating drag handles")
            handles.clear()

            when (shapeDef) {
                is CircleDef -> {
                    handles.add(RadiusHandle(fixtureParameter.circleCenterP.xP, fixtureParameter.circleCenterP.yP, fixtureParameter.circleRadiusP))
                    handles.add(PositionHandle(fixtureParameter.circleCenterP))
                }
                is BoxDef -> {
                    val corner1 = CornerHandle(fixtureParameter.boxCenterP, fixtureParameter.boxSizeP.xP, fixtureParameter.boxSizeP.yP, fixtureParameter.boxAngleP, null)
                    val corner2 = CornerHandle(fixtureParameter.boxCenterP, fixtureParameter.boxSizeP.xP, fixtureParameter.boxSizeP.yP, fixtureParameter.boxAngleP, corner1)
                    handles.add(corner1)
                    handles.add(corner2)
                }
                is PolygonDef -> {
                    fixtureParameter.polygonPointsP.innerParameters.forEach { pointP ->
                        handles.add(PositionHandle(pointP))
                    }
                }
            }

        }

        dirty = true
    }

    fun redraw() {

        dirty = false
        //println("Redraw")

        val shapeDef = currentShapedDef

        with(canvas.graphicsContext2D) {
            save()
            clearRect(0.0, 0.0, canvas.width, canvas.height)
            lineWidth = 1.0
            stroke = borderColor

            translate(margin, margin)
            strokeRect(0.0, 0.0, poseWidth.toDouble(), poseHeight.toDouble())

            save()
            translate(pose.offsetX, pose.offsetY)
            save()

            when (shapeDef) {
                is CircleDef -> {
                    drawOutlined(shapeColor) {
                        strokeOval(shapeDef.center.x - shapeDef.radius, shapeDef.center.y - shapeDef.radius, shapeDef.radius * 2, shapeDef.radius * 2)
                    }
                }
                is BoxDef -> {
                    translate(shapeDef.center.x, shapeDef.center.y)
                    rotate(shapeDef.angle.degrees)
                    drawOutlined(shapeColor) {
                        strokeRect(-shapeDef.width / 2, -shapeDef.height / 2, shapeDef.width, shapeDef.height)
                    }
                }
                is PolygonDef -> {
                    lineCap = StrokeLineCap.ROUND
                    drawOutlined(shapeColor) {
                        val xs = DoubleArray(shapeDef.points.size, { i -> shapeDef.points.map { it.x }[i] })
                        val ys = DoubleArray(shapeDef.points.size, { i -> shapeDef.points.map { it.y }[i] })
                        strokePolygon(xs, ys, shapeDef.points.size)
                    }
                }
            }

            restore()

            handles.forEach { it.draw() }

            restore()

            this.globalAlpha = 0.5
            drawImage(pose.image(),
                    pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                    0.0, 0.0, pose.rect.width.toDouble(), pose.rect.height.toDouble())
            this.globalAlpha = 1.0

            restore()
        }
    }

    fun drawOutlined(color: Color, shape: () -> Unit) {
        with(canvas.graphicsContext2D) {
            stroke = Color.BLACK
            lineCap = StrokeLineCap.ROUND
            lineWidth = 2.0
            shape()
            stroke = color
            lineWidth = 1.0
            shape()
        }
    }

    inner abstract class Handle {

        abstract fun position(): Vector2d

        fun draw() {
            with(canvas.graphicsContext2D) {
                save()
                val position = position()
                translate(position.x, position.y)
                drawOutlined(if (this@Handle == currentHandle) currentHandleColor else handleColor) {
                    strokeRect(-3.0, -3.0, 6.0, 6.0)
                }
                restore()
            }
        }

        abstract fun moveTo(x: Double, y: Double)
    }

    inner class PositionHandle(val parameter: Vector2dParameter) : Handle() {
        override fun position() = Vector2d(parameter.x ?: 0.0, parameter.y ?: 0.0)

        override fun moveTo(x: Double, y: Double) {
            parameter.xP.value = x
            parameter.yP.value = y
        }
    }

    inner class RadiusHandle(val centerXP: DoubleParameter, val centerYP: DoubleParameter, val radiusParameter: DoubleParameter) : Handle() {
        override fun position() = Vector2d((centerXP.value ?: 0.0) + (radiusParameter.value ?: 0.0), (centerYP.value ?: 0.0))

        override fun moveTo(x: Double, y: Double) {
            radiusParameter.value = x - (centerXP.value ?: 0.0)
        }
    }

    inner class CornerHandle(
            val centerParameter: Vector2dParameter,
            val widthParameter: DoubleParameter,
            val heightParameter: DoubleParameter,
            val angleParameter: AngleParameter,
            other: CornerHandle?)
        : Handle() {

        lateinit var other: CornerHandle

        var plusX: Boolean = true
        var plusY: Boolean = true

        init {
            if (other != null) {
                this.other = other
                this.other.other = this
                plusX = false
                plusY = false
            }
        }

        override fun position(): Vector2d {
            with(canvas.graphicsContext2D) {
                val scaleX = if (plusX) 0.5 else -0.5
                val scaleY = if (plusY) 0.5 else -0.5
                val width = widthParameter.value ?: 0.0
                val height = heightParameter.value ?: 0.0
                val cos = Math.cos(angleParameter.value.radians)
                val sin = Math.sin(angleParameter.value.radians)
                val dx = width * scaleX * cos - height * scaleY * sin
                val dy = height * scaleY * cos + width * scaleX * sin
                return Vector2d((centerParameter.x ?: 0.0) + dx, (centerParameter.y ?: 0.0) + dy)
            }
        }


        override fun moveTo(x: Double, y: Double) {
            val rotate = Matrix3x2d().rotate(-angleParameter.value.radians)
            val rotated = Vector2d(x - (centerParameter.x ?: 0.0), y - (centerParameter.y ?: 0.0))
            rotate.transformPosition(rotated)

            val otherRotated = other.position()
            otherRotated.x -= (centerParameter.x ?: 0.0)
            otherRotated.y -= (centerParameter.y ?: 0.0)

            rotate.transformPosition(otherRotated)

            val oldWidth = widthParameter.value ?: 0.0
            val oldHeight = heightParameter.value ?: 0.0

            var width = Math.round(rotated.x - otherRotated.x).toDouble() * if (plusX) 1 else -1
            var height = Math.round(rotated.y - otherRotated.y).toDouble() * if (plusY) 1 else -1

            if (width < 0) {
                width = -width
                plusX = !plusX
                other.plusX = !plusX
            }
            if (height < 0) {
                height = -height
                plusY = !plusY
                other.plusY = !plusY
            }

            centerParameter.x = (centerParameter.x ?: 0.0) + (width - oldWidth) / 2 * if (plusX) 1 else -1
            centerParameter.y = (centerParameter.y ?: 0.0) + (height - oldHeight) / 2 * if (plusY) 1 else -1

            widthParameter.value = width
            heightParameter.value = height
        }
    }

}
