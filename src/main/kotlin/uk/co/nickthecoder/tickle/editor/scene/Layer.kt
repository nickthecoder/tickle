package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.tickle.Resources

abstract class Layer {

    val canvas = Canvas(Resources.instance.gameInfo.width.toDouble(), Resources.instance.gameInfo.height.toDouble())

    var panX = 0.0
    var panY = 0.0

    fun draw() {
        val gc = canvas.graphicsContext2D

        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        gc.save()
        gc.transform(1.0, 0.0, 0.0, -1.0, 0.0, canvas.height)
        gc.translate(panX, panY)

        drawContent()
        gc.restore()

    }

    fun panBy(dx: Double, dy: Double) {
        panX += dx
        panY += dy
        draw()
    }

    abstract fun drawContent()

}
