package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.AttributeData
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.SceneActor

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

                translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                rotate(sceneActor.directionDegrees - (sceneActor.pose?.directionDegrees ?: 0.0))

                sceneActor.pose?.let { pose ->
                    lineWidth = selectionWidth + 2
                    stroke = Color.BLACK
                    drawBoundingBox(pose)
                    lineWidth = selectionWidth
                    stroke = if (sceneActor === selection.latest()) latestColor else selectionColor
                    drawBoundingBox(pose)
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

    fun drawBoundingBox(pose: Pose) {
        val margin = 2.0

        with(canvas.graphicsContext2D) {

            strokeRect(
                    -pose.offsetX.toDouble() - margin,
                    -pose.offsetY.toDouble() - margin,
                    pose.rect.width.toDouble() + margin * 2,
                    pose.rect.height.toDouble() + margin * 2)
        }
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
                    AttributeType.NORMAL -> {
                    }
                }
            }
        }

        dirty = true
    }

    companion object {
        var borderColor = Color.LIGHTCORAL
        var borderWidth = 1.0

        var selectionColor = Color.web("#0000c0")
        var latestColor = Color.web("#8000ff")
        var selectionWidth = 3.0

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
                stroke = Color.BLACK
                lineCap = StrokeLineCap.ROUND
                lineWidth = 4.0
                drawSimpleArrow(length)
                if (hovering) {
                    stroke = hightlightColor
                } else {
                    stroke = latestColor
                }
                lineWidth = 3.0
                drawSimpleArrow(length)
                restore()
            }
        }

        fun drawSimpleArrow(length: Double) {
            with(canvas.graphicsContext2D) {
                strokeLine(0.0, 0.0, length - 3, 0.0)
                strokeLine(length, 0.0, length - arrowSize, -arrowSize / 2)
                strokeLine(length, 0.0, length - arrowSize, +arrowSize / 2)
            }
        }

    }

    inner class RotateArrow(sceneActor: SceneActor) : Arrow(sceneActor, 0) {
        override fun get() = sceneActor.directionDegrees
        override fun set(degrees: Double) {
            sceneActor.directionDegrees = degrees
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - sceneActor.x
            val dy = y - sceneActor.y

            val atan = Math.atan2(dy, dx)
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (snap) 15.0 else 1.0))

            val degrees = angle - sceneActor.directionRadians

            selection.forEach {
                it.directionRadians += degrees
            }
        }

    }

    inner class DirectionArrow(sceneActor: SceneActor, val data: AttributeData, distance: Int)

        : Arrow(sceneActor, distance) {

        override fun get() = data.value?.toDouble() ?: 0.0

        override fun set(degrees: Double) {
            (data.parameter as DoubleParameter).value = degrees
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

}
