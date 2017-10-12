package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.SceneActor
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextStyle

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

    fun drawActor(sceneActor: SceneActor) {

        val pose = sceneActor.pose

        with(canvas.graphicsContext2D) {
            save()
            translate(sceneActor.x.toDouble(), sceneActor.y.toDouble())
            rotate(sceneActor.direction.degrees - (pose?.direction?.degrees ?: 0.0))
            if (pose == null) {
                sceneActor.textStyle?.let {
                    val text = if (sceneActor.text.isBlank()) "<no text>" else sceneActor.text
                    drawText(it, text)
                }
            } else {
                drawPose(pose)
            }
            restore()
        }
    }

    fun drawPose(pose: Pose, color: Color = Color.WHITE) {
        val image = pose.image()
        canvas.graphicsContext2D.drawImage(
                image,
                pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                -pose.offsetX.toDouble(), -pose.offsetY.toDouble(), pose.rect.width.toDouble(), pose.rect.height.toDouble())
    }

    fun drawText(textStyle: TextStyle, text: String) {

        val fontTexture = textStyle.fontResource.fontTexture
        var dx = 0.0
        text.forEach { c ->
            if (c == '\n') {
                canvas.graphicsContext2D.translate(-dx, fontTexture.lineHeight.toDouble())
                dx = 0.0
            } else {
                fontTexture.glyphs[c]?.let { glyph ->
                    drawPose(glyph.pose)
                    canvas.graphicsContext2D.translate(glyph.advance.toDouble(), 0.0)
                    dx += glyph.advance.toDouble()
                }
            }
        }
    }

}
