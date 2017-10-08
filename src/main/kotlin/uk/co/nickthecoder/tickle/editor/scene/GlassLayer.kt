package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.util.PolarParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter

class GlassLayer(val sceneResource: SceneResource, val selection: Selection)

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

                translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                rotate(sceneActor.direction.degrees - (sceneActor.pose?.direction?.degrees ?: 0.0))

                sceneActor.pose?.let { pose ->
                    drawOutlinedBoundingBox(pose, sceneActor === selection.latest())
                }

                restore()
            }
            restore()
        }

        dragHandles.forEach {
            it.draw()
        }

        newActor?.let {
            canvas.graphicsContext2D.globalAlpha = 0.5
            drawActor(it)
            canvas.graphicsContext2D.globalAlpha = 1.0
        }

        dirty = false
    }


    fun hover(x: Double, y: Double) {
        dragHandles.forEach {
            it.hover(x, y)
        }
    }

    override fun selectionChanged() {
        dragHandles.clear()
        val latest = selection.latest()
        if (latest != null) {
            if (latest.costume()?.canRotate == true) {
                dragHandles.add(RotateArrow(latest))
            }

            latest.attributes.data().forEach { data ->
                when (data.attributeType) {
                    AttributeType.DIRECTION -> {
                        dragHandles.add(DirectionArrow(latest, data, data.order))
                    }
                    AttributeType.POLAR -> {
                        dragHandles.add(PolarArrow(latest, data))
                    }
                    AttributeType.ABSOLUTE_POSITION -> {
                        dragHandles.add(AbsoluteHandle(latest, data))
                    }
                    AttributeType.RELATIVE_POSITION -> {
                        dragHandles.add(RelativeHandle(latest, data))
                    }
                    AttributeType.NORMAL -> {
                    }
                }
            }
        }

        dirty = true
    }


    fun drawOutlinedBoundingBox(pose: Pose, highlight: Boolean) {

        with(canvas.graphicsContext2D) {
            lineWidth = 4.0
            stroke = Color.BLACK
            drawSimpleBoundingBox(pose)
            lineWidth = 2.5
            stroke = if (highlight) latestColor else selectionColor
            drawSimpleBoundingBox(pose)
        }
    }

    fun drawSimpleBoundingBox(pose: Pose) {
        val margin = 2.0

        with(canvas.graphicsContext2D) {

            strokeRect(
                    -pose.offsetX.toDouble() - margin,
                    -pose.offsetY.toDouble() - margin,
                    pose.rect.width.toDouble() + margin * 2,
                    pose.rect.height.toDouble() + margin * 2)
        }
    }


    fun drawOutlinesArrow(length: Double, hovering: Boolean) {
        with(canvas.graphicsContext2D) {
            stroke = Color.BLACK
            lineCap = StrokeLineCap.ROUND
            lineWidth = 4.0
            drawSimpleArrow(length)
            if (hovering) {
                stroke = hightlightColor
            } else {
                stroke = latestColor
            }
            lineWidth = 2.5
            drawSimpleArrow(length)
        }
    }

    fun drawSimpleArrow(length: Double) {
        with(canvas.graphicsContext2D) {
            strokeLine(0.0, 0.0, length - 3, 0.0)
            strokeLine(length, 0.0, length - uk.co.nickthecoder.tickle.editor.scene.GlassLayer.Companion.arrowSize, -uk.co.nickthecoder.tickle.editor.scene.GlassLayer.Companion.arrowSize / 2)
            strokeLine(length, 0.0, length - uk.co.nickthecoder.tickle.editor.scene.GlassLayer.Companion.arrowSize, +uk.co.nickthecoder.tickle.editor.scene.GlassLayer.Companion.arrowSize / 2)
        }
    }

    fun drawOutlinesHandle(hovering: Boolean) {
        with(canvas.graphicsContext2D) {
            stroke = Color.BLACK
            lineCap = StrokeLineCap.ROUND
            lineWidth = 4.0
            drawSimpleHandle()
            if (hovering) {
                stroke = hightlightColor
            } else {
                stroke = latestColor
            }
            lineWidth = 2.5
            drawSimpleHandle()
        }
    }

    fun drawSimpleHandle() {
        with(canvas.graphicsContext2D) {
            strokeRect(0.0, 0.0, 4.0, 4.0)
        }
    }

    companion object {
        var borderColor = Color.LIGHTCORAL
        var borderWidth = 1.0

        var selectionColor = Color.web("#0000c0")
        var latestColor = Color.web("#8000ff")

        var hightlightColor = Color.web("#80ff00")

        var directionLength = 40.0
        var directionExtra = 25.0
        var arrowSize = 10.0

    }

    private val dragHandles = mutableListOf<DragHandle>()

    fun findDragHandle(x: Double, y: Double): DragHandle? {
        return dragHandles.firstOrNull { it.isNear(x, y) }
    }

    interface DragHandle {

        fun draw()

        fun isNear(x: Double, y: Double): Boolean

        fun hover(x: Double, y: Double)

        fun moveTo(x: Double, y: Double, snap: Boolean)
    }

    abstract inner class AbstractDragHandle : DragHandle {

        var hovering: Boolean = false
            set(v) {
                if (v != field) {
                    field = v
                    dirty = true
                }
            }

        abstract fun x(): Double

        abstract fun y(): Double

        override fun hover(x: Double, y: Double) {
            hovering = isNear(x, y)
        }

        override fun isNear(x: Double, y: Double): Boolean {
            val dx = x - x()
            val dy = y - y()
            return dx * dx + dy * dy < 36 // 6 pixels
        }
    }

    abstract inner class Arrow(val sceneActor: SceneActor, val distance: Int) : AbstractDragHandle() {

        abstract fun set(degrees: Double)

        abstract fun get(): Double

        override fun x() = sceneActor.x + (directionLength + distance * directionExtra) * Math.cos(Math.toRadians(get()))

        override fun y() = sceneActor.y + (directionLength + distance * directionExtra) * Math.sin(Math.toRadians(get()))

        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                val length = directionLength + distance * directionExtra

                rotate(get())
                drawOutlinesArrow(length, hovering)
                restore()
            }
        }

    }

    inner class RotateArrow(sceneActor: SceneActor) : Arrow(sceneActor, 0) {

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

    }

    inner class DirectionArrow(sceneActor: SceneActor, val data: AttributeData, distance: Int)

        : Arrow(sceneActor, distance) {

        val parameter = data.parameter!! as DoubleParameter

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

    }

    inner class PolarArrow(val sceneActor: SceneActor, val data: AttributeData)

        : AbstractDragHandle() {

        val parameter = data.parameter!! as PolarParameter

        fun set(angleRadians: Double, magnitude: Double) {
            parameter.angle = Math.toDegrees(angleRadians)
            parameter.magnitude = magnitude
        }

        override fun x() = sceneActor.x + parameter.value.vector().x.toDouble() * data.scale
        override fun y() = sceneActor.y + parameter.value.vector().y.toDouble() * data.scale

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
                translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                val length = parameter.magnitude!! * data.scale

                rotate(parameter.value.angle.degrees)
                drawOutlinesArrow(length, hovering)
                restore()
            }
        }

    }

    open inner class AbsoluteHandle(val sceneActor: SceneActor, val data: AttributeData)

        : AbstractDragHandle() {

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
                drawOutlinesHandle(hovering)
                restore()
            }
        }

    }

    inner class RelativeHandle(sceneActor: SceneActor, data: AttributeData)

        : AbsoluteHandle(sceneActor, data) {

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
                drawOutlinesHandle(hovering)
                restore()
            }
        }

    }

}
