package uk.co.nickthecoder.tickle.editor.scene

import javafx.application.Platform
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap

class GlassLayer(val selection: Selection)

    : Layer(), SelectionListener {

    var dirty: Boolean = true

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

        fun drawArrow() {
            with(canvas.graphicsContext2D) {
                strokeLine(0.0, 0.0, directionLength - 3, 0.0)
                strokeLine(directionLength, 0.0, directionLength - arrowSize, -arrowSize / 2)
                strokeLine(directionLength, 0.0, directionLength - arrowSize, +arrowSize / 2)
            }
        }

        with(gc) {
            save()
            stroke = selectionColor
            lineWidth = selectionWidth
            val margin = 2.0
            setLineDashes(3.0, 10.0)

            selection.selected().forEach { sceneActor ->
                save()

                translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                // TODO Rotation
                sceneActor.pose?.let { pose ->
                    strokeRect(
                            -pose.offsetX.toDouble() - margin,
                            -pose.offsetY.toDouble() - margin,
                            pose.rect.width.toDouble() + margin * 2,
                            pose.rect.height.toDouble() + margin * 2)
                }

                restore()
            }
            restore()
        }

        with(gc) {
            save()
            selection.latest()?.let { sceneActor ->
                save()
                translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
                rotate(sceneActor.directionDegrees)
                stroke = Color.BLACK
                gc.lineCap = StrokeLineCap.ROUND
                lineWidth = 4.0
                drawArrow()
                stroke = selectionColor
                lineWidth = 3.0
                drawArrow()
                restore()
            }
            restore()
        }

        dirty = false
    }

    fun isNearRotationHandle(x: Float, y: Float): Boolean {
        selection.latest()?.let { sceneActor ->
            val hx = sceneActor.x + directionLength * Math.cos(sceneActor.directionRadians)
            val hy = sceneActor.y + directionLength * Math.sin(sceneActor.directionRadians)
            val dist2 = (x - hx) * (x - hx) + (y - hy) * (y - hy)
            return dist2 < 10.0
        }
        return false
    }

    override fun selectionChanged() {
        dirty = true
        Platform.runLater {
            if (dirty) {
                draw()
            }
        }
    }

    companion object {
        var borderColor = Color.LIGHTCORAL
        var borderWidth = 1.0

        var selectionColor = Color.BLUEVIOLET
        var selectionWidth = 2.0

        var directionLength = 40.0
        var arrowSize = 10.0
    }

}
