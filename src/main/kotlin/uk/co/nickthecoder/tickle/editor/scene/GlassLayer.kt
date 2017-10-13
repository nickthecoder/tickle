package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.editor.util.AngleParameter
import uk.co.nickthecoder.tickle.editor.util.PolarParameter
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter
import uk.co.nickthecoder.tickle.editor.util.costume

class GlassLayer(val sceneResource: SceneResource, val selection: Selection)

    : Layer(), SelectionListener, SceneResourceListener {

    var dirty = false
        set(v) {
            if (field != v) {
                field = v

                Platform.runLater {
                    if (dirty) {
                        draw()
                    }
                }
            }
        }

    var newActor: ActorResource? = null

    init {
        selection.listeners.add(this)
        sceneResource.listeners.add(this)
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

        // Dotted lines around each selected ActorResource
        with(gc) {
            save()
            setLineDashes(3.0, 10.0)

            selection.selected().forEach { actorResource ->
                save()

                translate(actorResource.x, actorResource.y)
                rotate(actorResource.direction.degrees - (actorResource.pose?.direction?.degrees ?: 0.0))

                drawOutlined(selectionColor(actorResource === selection.latest())) { drawBoundingBox(actorResource) }

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

    override fun actorModified(sceneResource: SceneResource, actorResource: ActorResource, type: ModificationType) {
        dirty = true
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

    fun drawBoundingBox(actorResource: ActorResource) {
        val margin = 2.0

        actorResource.pose?.let { pose ->

            canvas.graphicsContext2D.strokeRect(
                    -pose.offsetX - margin,
                    -pose.offsetY - margin,
                    pose.rect.width.toDouble() + margin * 2,
                    pose.rect.height.toDouble() + margin * 2)

            return
        }

        actorResource.textStyle?.let { textStyle ->
            val text = actorResource.displayText
            val offestX = textStyle.offsetX(text)
            val offsetY = textStyle.offsetY(text)
            val width = textStyle.width(text)
            val height = textStyle.height(text)

            canvas.graphicsContext2D.strokeRect(offestX, offsetY, width, height)
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

    abstract inner class Arrow(name: String, val actorResource: ActorResource, val distance: Int)

        : AbstractDragHandle(name) {

        abstract fun set(degrees: Double)

        abstract fun get(): Double

        override fun x() = actorResource.x + (directionLength + distance * directionExtra) * Math.cos(Math.toRadians(get()))

        override fun y() = actorResource.y + (directionLength + distance * directionExtra) * Math.sin(Math.toRadians(get()))

        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(actorResource.x, actorResource.y)
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

    inner class RotateArrow(name: String, actorResource: ActorResource)

        : Arrow(name, actorResource, 0) {

        override fun get() = actorResource.direction.degrees

        override fun set(degrees: Double) {
            actorResource.direction.degrees = degrees
            sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - actorResource.x
            val dy = y - actorResource.y

            val atan = Math.atan2(dy, dx)
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (snap) 15.0 else 1.0))

            val degrees = angle - actorResource.direction.radians

            selection.forEach { actor ->
                actor.direction.radians += degrees
                sceneResource.fireChange(actor, ModificationType.CHANGE)
            }
        }

        override fun drawArrowHandle(length: Double) {
            lineWithHandle(length) { drawRoundHandle() }
        }

    }

    inner class DirectionArrow(name: String, actorResource: ActorResource, val data: AttributeData, distance: Int)

        : Arrow(name, actorResource, distance) {

        val parameter = if (data.parameter is AngleParameter)
            (data.parameter as AngleParameter).degreesP
        else
            data.parameter as DoubleParameter

        override fun get() = parameter.value ?: 0.0

        override fun set(degrees: Double) {
            parameter.value = degrees
            sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - actorResource.x
            val dy = y - actorResource.y

            val atan = Math.atan2(dy, dx)
            var angle = if (atan < 0) atan + Math.PI * 2 else atan

            angle -= angle.rem(Math.toRadians(if (snap) 15.0 else 1.0))
            set(Math.toDegrees(angle))
        }


        override fun drawArrowHandle(length: Double) {
            lineWithHandle(length) { drawDiamondHandle() }
        }
    }

    inner class PolarArrow(name: String, val actorResource: ActorResource, val data: AttributeData)

        : AbstractDragHandle(name) {

        val parameter = data.parameter!! as PolarParameter

        fun set(angleRadians: Double, magnitude: Double) {
            parameter.angle = Math.toDegrees(angleRadians)
            parameter.magnitude = magnitude
            sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        }

        override fun x() = actorResource.x + parameter.value.vector().x * data.scale
        override fun y() = actorResource.y + parameter.value.vector().y * data.scale

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - actorResource.x
            val dy = y - actorResource.y

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
                translate(actorResource.x, actorResource.y)
                val length = parameter.magnitude!! * data.scale

                rotate(parameter.value.angle.degrees)
                drawOutlined(handleColor(hovering)) { lineWithHandle(length) }
                restore()
            }
        }

    }

    open inner class AbsoluteHandle(name: String, val actorResource: ActorResource, val data: AttributeData)

        : AbstractDragHandle(name) {

        val parameter = data.parameter!! as Vector2dParameter

        fun set(x: Double, y: Double) {
            parameter.x = x
            parameter.y = y
            sceneResource.fireChange(actorResource, ModificationType.CHANGE)
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

    inner class RelativeHandle(name: String, actorResource: ActorResource, data: AttributeData)

        : AbsoluteHandle(name, actorResource, data) {

        override fun x() = actorResource.x + super.x() * data.scale
        override fun y() = actorResource.y + super.y() * data.scale

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            var dx = (x - actorResource.x) / data.scale
            var dy = (y - actorResource.y) / data.scale

            if (snap) {
                dx = Math.floor(dx)
                dy = Math.floor(dy)
            }

            set(dx, dy)
        }


        override fun draw() {

            with(canvas.graphicsContext2D) {
                save()
                translate(actorResource.x + parameter.x!! * data.scale, actorResource.y + parameter.y!! * data.scale)
                drawOutlined(handleColor(hovering)) { drawDiamondHandle() }
                restore()
            }
        }

    }

}
