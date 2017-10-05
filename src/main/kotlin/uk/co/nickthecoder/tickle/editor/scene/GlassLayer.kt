package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import uk.co.nickthecoder.tickle.Pose

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

    var highlightRotation = true
        set(v) {
            if (v != field) {
                field = v
                dirty = true
            }
        }

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

        with(gc) {
            save()
            selection.latest()?.let { sceneActor ->

                if (sceneActor.costume()?.canRotate == true) {
                    save()
                    translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                    rotate(sceneActor.directionDegrees)
                    stroke = Color.BLACK
                    gc.lineCap = StrokeLineCap.ROUND
                    lineWidth = 4.0
                    drawArrow()
                    if (highlightRotation) {
                        stroke = hightlightColor
                    } else {
                        stroke = latestColor
                    }
                    lineWidth = 3.0
                    drawArrow()
                    restore()
                }

            }
            restore()
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

    fun drawArrow() {
        with(canvas.graphicsContext2D) {
            strokeLine(0.0, 0.0, directionLength - 3, 0.0)
            strokeLine(directionLength, 0.0, directionLength - arrowSize, -arrowSize / 2)
            strokeLine(directionLength, 0.0, directionLength - arrowSize, +arrowSize / 2)
        }
    }

    fun highlightHandle(x: Float, y: Float) {
        highlightRotation = isNearRotationHandle(x, y)
    }

    fun isNearRotationHandle(x: Float, y: Float): Boolean {
        selection.latest()?.let { sceneActor ->
            if (sceneActor.costume()?.canRotate != true) {
                return false
            }
            val hx = sceneActor.x + directionLength * Math.cos(sceneActor.directionRadians)
            val hy = sceneActor.y + directionLength * Math.sin(sceneActor.directionRadians)
            val dist2 = (x - hx) * (x - hx) + (y - hy) * (y - hy)
            return dist2 < 36.0 // 6 pixels
        }
        return false
    }

    override fun selectionChanged() {
        highlightRotation = false
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
        var arrowSize = 10.0
    }

}
