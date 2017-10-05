package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.paint.Color

class GlassLayer() : Layer() {

    override fun drawContent() {
        val gc = canvas.graphicsContext2D

        // A dashed border the size of the game window, with the bottom left at (0,0)
        gc.stroke = Color.LIGHTCORAL
        gc.lineWidth = 1.0
        gc.setLineDashes(10.0, 3.0)
        gc.strokeLine(-1.0, -1.0, canvas.width + 1, -1.0)
        gc.strokeLine(canvas.width + 1, -1.0, canvas.width, canvas.height + 1)
        gc.strokeLine(canvas.width + 1, canvas.height + 1, -1.0, canvas.height + 1)
        gc.strokeLine(-1.0, canvas.height + 1, -1.0, -1.0)
        gc.setLineDashes()
    }
}
