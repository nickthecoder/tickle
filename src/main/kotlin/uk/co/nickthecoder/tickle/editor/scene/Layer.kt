package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.util.image
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.TextStyle
import uk.co.nickthecoder.tickle.resources.ActorResource
import uk.co.nickthecoder.tickle.resources.Resources

abstract class Layer {

    val canvas = Canvas(Resources.instance.gameInfo.width.toDouble(), Resources.instance.gameInfo.height.toDouble())

    // The center of the view in world coordinates, so that when scaling, the center will stay in the same
    // place in the world view.
    var centerX = canvas.width / 2
    var centerY = canvas.height / 2
    var scale: Double = 1.0

    fun draw() {
        val gc = canvas.graphicsContext2D

        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        gc.save()
        gc.transform(1.0, 0.0, 0.0, -1.0, 0.0, canvas.height)
        gc.scale(scale, scale)
        gc.translate((-centerX + canvas.width / scale / 2), (-centerY + canvas.height / scale / 2))

        drawContent()
        gc.restore()
    }

    fun scale(scale: Double) {
        this.scale = scale
        draw()
    }

    fun panBy(dx: Double, dy: Double) {
        centerX -= dx / scale
        centerY -= dy / scale
        draw()
    }

    abstract fun drawContent()

    fun drawActor(actorResource: ActorResource) {

        val pose = actorResource.editorPose

        with(canvas.graphicsContext2D) {
            save()
            translate(actorResource.x.toDouble(), actorResource.y.toDouble())
            rotate(actorResource.direction.degrees - (pose?.direction?.degrees ?: 0.0))
            scale(actorResource.scale.x, actorResource.scale.y)
            if (pose == null) {
                actorResource.textStyle?.let {
                    drawText(it, actorResource.displayText)
                }
            } else {
                drawPose(pose)
            }
            restore()
        }
    }

    private val WHITE = Color.white()

    fun drawPose(pose: Pose) {
        val image = pose.image()
        canvas.graphicsContext2D.drawImage(
                image,
                pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                -pose.offsetX, -pose.offsetY, pose.rect.width.toDouble(), pose.rect.height.toDouble())
    }

    fun drawText(textStyle: TextStyle, text: String) {

        val fontTexture = textStyle.fontResource.fontTexture
        //textStyle.offsetY(text) - textStyle.height(text)
        canvas.graphicsContext2D.translate(0.0, textStyle.offsetY(text))
        var dx = 0.0

        text.split('\n').forEach { line ->
            val offsetX = -textStyle.offsetX(line)
            canvas.graphicsContext2D.translate(offsetX, 0.0)

            line.forEach { c ->
                fontTexture.glyphs[c]?.let { glyph ->
                    drawPose(glyph.pose)
                    dx += glyph.advance
                    canvas.graphicsContext2D.translate(glyph.advance, 0.0)
                }
            }
            canvas.graphicsContext2D.translate(-dx - offsetX, -fontTexture.lineHeight)
            dx = 0.0
        }
    }

}
