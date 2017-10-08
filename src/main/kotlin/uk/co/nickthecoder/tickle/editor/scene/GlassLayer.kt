package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.AttributeData
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.editor.util.AngleParameter
import uk.co.nickthecoder.tickle.editor.util.PolarParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter

class GlassLayer(val selection: Selection)

    : Layer(), SelectionListener {

    var dirty = true
        set(v) {
            field = v
            Platform.runLater {
                if (dirty) {
                    draw()
                }
            }
        }

    var newActor: SceneActor? = null

    init {
        selection.listeners.add(this)
    }

    override fun drawContent() {
        val gc = canvas.graphicsContext2D

        // A dashed border the size of the game window, with the bottom left at (0,0)
        with(gc) {
            save()
            stroke = borderColor
            lineWidth = borderWidth
            setLineDashes(10.0, 3.0)

            strokeLine(-1.0, -1.0, canvas.width + 1, -1.0)
            strokeLine(canvas.width + 1, -1.0, canvas.width, canvas.height + 1)
            strokeLine(canvas.width + 1, canvas.height + 1, -1.0, canvas.height + 1)
            strokeLine(-1.0, canvas.height + 1, -1.0, -1.0)
            setLineDashes()

            restore()
        }

        with(gc) {
            save()
            setLineDashes(3.0, 10.0)

            selection.selected().forEach { sceneActor ->
                save()

                translate(sceneActor.x, sceneActor.y)
                rotate(sceneActor.direction.degrees - (sceneActor.pose?.direction?.degrees ?: 0.0))

                sceneActor.pose?.let { pose ->
                    drawOutlined(selectionColor(sceneActor === selection.latest())) { drawBoundingBox(pose) }
                }

                restore()
            }
            restore()
        }

        dragHandles.forEach { handle ->
            handle.draw()
        }

        dragHandles.firstOrNull { it.hovering }?.let { handle ->
            with(canvas.graphicsContext2D) {
                save()
                translate(handle.x(), handle.y())
                scale(1.0, -1.0)
                lineWidth = 3.0
                stroke = Color.BLACK
                strokeText(handle.name, 20.0, 0.0)
                fill = latestColor
                fillText(handle.name, 20.0, 0.0)
                restore()
            }
        }

        newActor?.let {
            canvas.graphicsContext2D.globalAlpha = 0.5
            drawActor(it)
            canvas.graphicsContext2D.globalAlpha = 1.0
        }

        dirty = false
    }


    fun hover(x: Double, y: Double) {
        dragHandles.forEach { handle ->
            if (handle.hover(x, y)) {
                dragHandles.filter { it !== handle }.forEach { it.hover(Double.MAX_VALUE, 0.0) } // Turn the others off!
                return
            }
        }
    }

    override fun selectionChanged() {
        dragHandles.clear()
        val latest = selection.latest()
        if (latest != null) {
            if (latest.costume()?.canRotate == true) {
                dragHandles.add(RotateArrow("Direction", latest))
            }

            latest.attributes.map().forEach { name, data ->
                when (data.attributeType) {
                    AttributeType.DIRECTION -> {
                        dragHandles.add(DirectionArrow(name, latest, data, data.order))
                    }
                    AttributeType.POLAR -> {
                        dragHandles.add(PolarArrow(name, latest, data))
                    }
                    AttributeType.ABSOLUTE_POSITION -> {
                        dragHandles.add(AbsoluteHandle(name, latest, data))
                    }
                    AttributeType.RELATIVE_POSITION -> {
                        dragHandles.add(RelativeHandle(name, latest, data))
                    }
                    AttributeType.NORMAL -> {
                    }
                }
            }
        }

        dirty = true
    }

    fun drawBoundingBox(pose: Pose) {
        val margin = 2.0

        with(canvas.graphicsContext2D) {

            strokeRect(
                    -pose.offsetX - margin,
                    -pose.offsetY - margin,
                    pose.rect.width.toDouble() + margin * 2,
                    pose.rect.height.toDouble() + margin * 2)
        }
    }

    fun lineWithHandle(length: Double, handleShape: () -> Unit = { drawArrowHead() }) {
        with(canvas.graphicsContext2D) {
            save()
            strokeLine(0.0, 0.0, length - 3, 0.0)
            translate(length, 0.0)
            handleShape()
            restore()
        }
    }

    fun drawArrowHead() {
        with(canvas.graphicsContext2D) {
            strokeLine(0.0, 0.0, -arrowSize, -arrowSize / 2)
            strokeLine(0.0, 0.0, -arrowSize, arrowSize / 2)
        }
    }

    fun drawRoundHandle() {
        with(canvas.graphicsContext2D) {
            strokeOval(-3.0, -3.0, 6.0, 6.0)
        }
    }

    fun drawDiamondHandle() {
        with(canvas.graphicsContext2D) {
            save()
            rotate(45.0)
            drawSquareHandle()
            restore()
        }
    }

    fun drawSquareHandle() {
        with(canvas.graphicsContext2D) {
            strokeRect(-2.5, -2.5, 5.0, 5.0)
        }
    }

    fun drawOutlined(color: Color, shape: () -> Unit) {
        with(canvas.graphicsContext2D) {
            stroke = Color.BLACK
            lineCap = StrokeLineCap.ROUND
            lineWidth = 4.0
            shape()
            stroke = color
            lineWidth = 2.5
            shape()
        }
    }

    companion object {
        var borderColor: Color = Color.LIGHTCORAL
        var borderWidth = 1.0

        var selectionColor: Color = Color.web("#0000c0")
        var latestColor: Color = Color.web("#8000ff")

        var hightlightColor: Color = Color.web("#80ff00")

        var directionLength = 40.0
        var directionExtra = 15.0
        var arrowSize = 10.0

        fun handleColor(hovering: Boolean) = if (hovering) hightlightColor else latestColor

        fun selectionColor(latest: Boolean) = if (latest) latestColor else selectionColor

    }

    private val dragHandles = mutableListOf<DragHandle>()

    fun findDragHandle(x: Double, y: Double): DragHandle? {
        return dragHandles.firstOrNull { it.isNear(x, y) }
    }

    interface DragHandle {

        val hovering: Boolean

        val name: String

        fun draw()

        fun x(): Double

        fun y(): Double

        fun isNear(x: Double, y: Double): Boolean

        fun hover(x: Double, y: Double): Boolean

        fun moveTo(x: Double, y: Double, snap: Boolean)
    }

    abstract inner class AbstractDragHandle(override val name: String) : DragHandle {

        override var hovering: Boolean = false
            set(v) {
                if (v != field) {
                    field = v
                    dirty = true
                }
            }

        override fun hover(x: Double, y: Double): Boolean {
            hovering = isNear(x, y)
            return hovering
        }

        override fun isNear(x: Double, y: Double): Boolean {
            val dx = x - x()
            val dy = y - y()
            return dx * dx + dy * dy < 36 // 6 pixels
        }

    }

    abstract inner class Arrow(name: String, val sceneActor: SceneActor, val distance: Int)

        : AbstractDragHandle(name) {

        abstract fun set(degrees: Double)

        abstract fun get(): Double

        override fun x() = sceneActor.x + (directionLength + distance * directionExtra) * Math.cos(Math.toRadians(get()))

        override fun y() = sceneActor.y + (directionLength + distance * directionExtra) * Math.sin(Math.toRadians(get()))

        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(sceneActor.x, sceneActor.y)
                val length = directionLength + distance * directionExtra

                rotate(get())
                drawOutlined(handleColor(hovering)) { drawArrowHandle(length) }
                restore()
            }
        }

        open fun drawArrowHandle(length: Double) {
            lineWithHandle(length)
        }

    }

    inner class RotateArrow(name: String, sceneActor: SceneActor)

        : Arrow(name, sceneActor, 0) {

        override fun get() = sceneActor.direction.degrees

        override fun set(degrees: Double) {
            sceneActor.direction.degrees = degrees
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - sceneActor.x
            val dy = y - sceneActor.y

            val atan = Math.atan2(dy, dx)
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (snap) 15.0 else 1.0))

            val degrees = angle - sceneActor.direction.radians

            selection.forEach {
                it.direction.radians += degrees
            }
        }

        override fun drawArrowHandle(length: Double) {
            lineWithHandle(length) { drawRoundHandle() }
        }

    }

    inner class DirectionArrow(name: String, sceneActor: SceneActor, val data: AttributeData, distance: Int)

        : Arrow(name, sceneActor, distance) {

        val parameter = if (data.parameter is AngleParameter)
            (data.parameter as AngleParameter).degreesP
        else
            data.parameter as DoubleParameter

        override fun get() = parameter.value ?: 0.0

        override fun set(degrees: Double) {
            parameter.value = degrees
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - sceneActor.x
            val dy = y - sceneActor.y

            val atan = Math.atan2(dy, dx)
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (snap) 15.0 else 1.0))
            set(Math.toDegrees(angle))
        }


        override fun drawArrowHandle(length: Double) {
            lineWithHandle(length) { drawDiamondHandle() }
        }
    }

    inner class PolarArrow(name: String, val sceneActor: SceneActor, val data: AttributeData)

        : AbstractDragHandle(name) {

        val parameter = data.parameter!! as PolarParameter

        fun set(angleRadians: Double, magnitude: Double) {
            parameter.angle = Math.toDegrees(angleRadians)
            parameter.magnitude = magnitude
        }

        override fun x() = sceneActor.x + parameter.value.vector().x * data.scale
        override fun y() = sceneActor.y + parameter.value.vector().y * data.scale

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - sceneActor.x
            val dy = y - sceneActor.y

            val atan = Math.atan2(dy, dx)
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (snap) 15.0 else 1.0))
            var mag = Math.sqrt(dx * dx + dy * dy) / data.scale
            if (snap) {
                mag = Math.floor(mag)
            }

            set(angle, mag)
        }


        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(sceneActor.x, sceneActor.y)
                val length = parameter.magnitude!! * data.scale

                rotate(parameter.value.angle.degrees)
                drawOutlined(handleColor(hovering)) { lineWithHandle(length) }
                restore()
            }
        }

    }

    open inner class AbsoluteHandle(name: String, val sceneActor: SceneActor, val data: AttributeData)

        : AbstractDragHandle(name) {

        val parameter = data.parameter!! as Vector2dParameter

        fun set(x: Double, y: Double) {
            parameter.x = x
            parameter.y = y

        }

        override fun x() = parameter.x!!
        override fun y() = parameter.y!!

        override fun moveTo(x: Double, y: Double, snap: Boolean) {
            set(x, y)
        }

        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(parameter.x!!, parameter.y!!)
                drawOutlined(handleColor(hovering)) { drawSquareHandle() }
                restore()
            }
        }

    }

    inner class RelativeHandle(name: String, sceneActor: SceneActor, data: AttributeData)

        : AbsoluteHandle(name, sceneActor, data) {

        override fun x() = sceneActor.x + super.x() * data.scale
        override fun y() = sceneActor.y + super.y() * data.scale

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            var dx = (x - sceneActor.x) / data.scale
            var dy = (y - sceneActor.y) / data.scale

            if (snap) {
                dx = Math.floor(dx)
                dy = Math.floor(dy)
            }

            set(dx, dy)
        }


        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(sceneActor.x + parameter.x!! * data.scale, sceneActor.y + parameter.y!! * data.scale)
                drawOutlined(handleColor(hovering)) { drawDiamondHandle() }
                restore()
            }
        }

    }

}
