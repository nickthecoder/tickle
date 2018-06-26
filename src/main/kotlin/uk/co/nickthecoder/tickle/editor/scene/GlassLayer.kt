package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.tickle.AttributeData
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.editor.scene.history.ChangeDoubleParameter
import uk.co.nickthecoder.tickle.editor.scene.history.ChangePolarParameter
import uk.co.nickthecoder.tickle.editor.scene.history.ChangeVector2dParameter
import uk.co.nickthecoder.tickle.editor.scene.history.Rotate
import uk.co.nickthecoder.tickle.editor.util.*
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.ModificationType
import uk.co.nickthecoder.tickle.resources.SceneResource
import uk.co.nickthecoder.tickle.resources.SceneResourceListener
import uk.co.nickthecoder.tickle.util.rotate

class GlassLayer(private val sceneEditor: SceneEditor)

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

    private val dragHandles = mutableListOf<DragHandle>()

    init {
        sceneEditor.selection.listeners.add(this)
        sceneEditor.sceneResource.listeners.add(this)
    }

    override fun drawContent() {
        val gc = canvas.graphicsContext2D

        // A dashed border the size of the game window, with the bottom left at (0,0)
        with(gc) {
            save()
            stroke = borderColor
            lineWidth = borderWidth
            setLineDashes(10.0 / scale, 3.0 / scale)

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

            sceneEditor.selection.selected().forEach { actorResource ->
                save()

                translate(actorResource.x, actorResource.y)
                rotate(actorResource.direction.degrees - (actorResource.editorPose?.direction?.degrees ?: 0.0))
                scale(actorResource.scale.x, actorResource.scale.y)

                drawOutlined(selectionColor(actorResource === sceneEditor.selection.latest())) { drawBoundingBox(actorResource) }

                restore()
            }
            restore()
        }
        // Name of the 'latest' actor
        sceneEditor.selection.latest()?.let { actor ->
            drawCostumeName(actor)
        }

        // Each drag handle
        dragHandles.forEach { handle ->
            handle.draw()
        }

        // Text for the handle the mouse is hovering over
        dragHandles.firstOrNull { it.hovering }?.let { handle ->
            with(canvas.graphicsContext2D) {
                save()
                translate(handle.x(), handle.y())
                outlinedText(handle.name)
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
        val latest = sceneEditor.selection.latest()
        if (latest != null) {
            if (latest.costume()?.canRotate == true) {
                dragHandles.add(RotateArrow("Direction", latest))
            }

            if (latest.isSizable()) {
                dragHandles.add(SizeHandle(latest, true, true))
                dragHandles.add(SizeHandle(latest, true, false))
                dragHandles.add(SizeHandle(latest, false, true))
                dragHandles.add(SizeHandle(latest, false, false))
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

    fun outlinedText(text: String) {
        with(canvas.graphicsContext2D) {
            scale(1.0 / scale, -1.0 / scale)
            lineWidth = 3.0
            stroke = Color.BLACK
            strokeText(text, 0.0, 0.0)
            fill = latestColor
            fillText(text, 0.0, 0.0)
        }
    }

    fun drawCostumeName(actorResource: ActorResource) {

        with(canvas.graphicsContext2D) {
            save()
            translate(actorResource.x - actorResource.offsetX(), actorResource.y - actorResource.offsetY() - 20.0)
            outlinedText(actorResource.costumeName)
            restore()
        }
    }

    fun drawBoundingBox(actorResource: ActorResource) {
        val margin = 2.0

        if (actorResource.isSizable()) {
            canvas.graphicsContext2D.strokeRect(
                    -actorResource.sizeAlignment.x * actorResource.size.x,
                    -actorResource.sizeAlignment.y * actorResource.size.y,
                    actorResource.size.x,
                    actorResource.size.y
            )
        } else {
            actorResource.editorPose?.let { pose ->

                canvas.graphicsContext2D.strokeRect(
                        -pose.offsetX - margin,
                        -pose.offsetY - margin,
                        pose.rect.width.toDouble() + margin * 2,
                        pose.rect.height.toDouble() + margin * 2)

                return
            }

            actorResource.textStyle?.let { textStyle ->
                val text = actorResource.displayText
                val offsetX = textStyle.offsetX(text)
                val offsetY = textStyle.offsetY(text)
                val width = textStyle.width(text)
                val height = textStyle.height(text)

                canvas.graphicsContext2D.strokeRect(-offsetX, offsetY - height, width, height)
            }
        }
    }

    fun lineWithHandle(length: Double, handleShape: () -> Unit = { drawArrowHead() }) {
        with(canvas.graphicsContext2D) {
            save()
            strokeLine(0.0, 0.0, length - 3 / scale, 0.0)
            translate(length, 0.0)
            handleShape()
            restore()
        }
    }

    fun drawArrowHead() {
        with(canvas.graphicsContext2D) {
            strokeLine(0.0, 0.0, -arrowSize / scale, -arrowSize / 2 / scale)
            strokeLine(0.0, 0.0, -arrowSize / scale, arrowSize / 2 / scale)
        }
    }

    fun drawRoundHandle() {
        with(canvas.graphicsContext2D) {
            strokeOval(-3.0 / scale, -3.0 / scale, 6.0 / scale, 6.0 / scale)
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
            strokeRect(-2.5 / scale, -2.5 / scale, 5.0 / scale, 5.0 / scale)
        }
    }

    fun drawCornerHandle() {
        with(canvas.graphicsContext2D) {
            strokeRect(1.0, -3.5 / scale, 2.0 / scale, 6.0 / scale)
            strokeRect(-3.5, 1.0 / scale, 6.0 / scale, 2.0 / scale)
        }
    }

    fun drawOutlined(color: Color, shape: () -> Unit) {
        with(canvas.graphicsContext2D) {
            stroke = Color.BLACK
            lineCap = StrokeLineCap.ROUND
            lineWidth = 4.0 / scale
            shape()
            stroke = color
            lineWidth = 2.5 / scale
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
            // I don't think this is used! ???
            sceneEditor.history.makeChange(Rotate(actorResource, degrees))
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val dx = x - actorResource.x
            val dy = y - actorResource.y

            val atan = Math.atan2(dy, dx)
            var degrees = Math.toDegrees(if (atan < 0) atan + Math.PI * 2 else atan)

            if (snap) {
                degrees = sceneEditor.sceneResource.snapRotation.snapRotation(degrees)
            }
            val diff = degrees - actorResource.direction.degrees

            sceneEditor.selection.forEach { actor ->
                sceneEditor.history.makeChange(Rotate(actorResource, actor.direction.degrees + diff))
            }
        }

        override fun drawArrowHandle(length: Double) {
            lineWithHandle(length) { drawRoundHandle() }
        }

    }

    inner class SizeHandle(val actorResource: ActorResource, val isLeft: Boolean, val isBottom: Boolean)
        : AbstractDragHandle("Resize") {

        override fun draw() {
            with(canvas.graphicsContext2D) {
                save()
                translate(x(), y())
                rotate(actorResource.direction.degrees - (actorResource.editorPose?.direction?.degrees ?: 0.0))
                if (isLeft) scale(-1.0, 1.0)
                if (isBottom) scale(1.0, -1.0)
                drawOutlined(handleColor(hovering)) { drawCornerHandle() }

                restore()
            }
        }

        override fun moveTo(x: Double, y: Double, snap: Boolean) {

            val now = Vector2d(x, y)
            viewToActor(actorResource, now)

            val was = Vector2d(x(), y())
            viewToActor(actorResource, was)

            val dx = now.x - was.x
            val dy = now.y - was.y

            if (isLeft) {
                actorResource.x += dx * (1 - actorResource.sizeAlignment.x)
                actorResource.size.x -= dx
            } else {
                actorResource.x += dx * actorResource.sizeAlignment.x
                actorResource.size.x += dx
            }

            if (isBottom) {
                actorResource.y += dy * (1 - actorResource.sizeAlignment.y)
                actorResource.size.y -= dy
            } else {
                actorResource.y += dy * actorResource.sizeAlignment.y
                actorResource.size.y += dy
            }
            actorResource.draggedX = actorResource.x
            actorResource.draggedY = actorResource.y
            sceneEditor.sceneResource.fireChange(actorResource, ModificationType.CHANGE)
        }

        override fun x(): Double {

            val vector = Vector2d(
                    (if (isLeft) 0.0 else actorResource.size.x) - actorResource.sizeAlignment.x * actorResource.size.x,
                    (if (isBottom) 0.0 else actorResource.size.y) - actorResource.sizeAlignment.y * actorResource.size.y)

            actorToView(actorResource, vector)
            return vector.x
        }

        override fun y(): Double {
            val vector = Vector2d(
                    (if (isLeft) 0.0 else actorResource.size.x) - actorResource.sizeAlignment.x * actorResource.size.x,
                    (if (isBottom) 0.0 else actorResource.size.y) - actorResource.sizeAlignment.y * actorResource.size.y)

            actorToView(actorResource, vector)
            return vector.y
        }

        fun actorToView(actorResource: ActorResource, vector: Vector2d) {
            vector.rotate(actorResource.direction.radians - (actorResource.editorPose?.direction?.radians ?: 0.0))
            vector.add(actorResource.x, actorResource.y)
        }

        fun viewToActor(actorResource: ActorResource, vector: Vector2d) {
            vector.add(-actorResource.x, -actorResource.y)
            vector.rotate(-actorResource.direction.radians + (actorResource.editorPose?.direction?.radians ?: 0.0))
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
            sceneEditor.history.makeChange(ChangeDoubleParameter(actorResource, parameter, degrees))
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
            sceneEditor.history.makeChange(ChangePolarParameter(actorResource, parameter, Math.toDegrees(angleRadians), magnitude))
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
            sceneEditor.history.makeChange(ChangeVector2dParameter(actorResource, parameter, x, y))
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
