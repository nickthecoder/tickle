/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.editor.scene

import javafx.scene.canvas.Canvas
import uk.co.nickthecoder.tickle.NinePatch
import uk.co.nickthecoder.tickle.Pose
import uk.co.nickthecoder.tickle.editor.util.cachedImage
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

    private val WHITE = Color.white()

    fun draw() {
        draw {
            drawContent()
        }
    }

    fun draw(drawing: () -> Unit) {
        val gc = canvas.graphicsContext2D

        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        gc.save()
        gc.transform(1.0, 0.0, 0.0, -1.0, 0.0, canvas.height)
        gc.scale(scale, scale)
        gc.translate((-centerX + canvas.width / scale / 2), (-centerY + canvas.height / scale / 2))

        drawing()
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

        val ninePatch = actorResource.ninePatch
        val pose = actorResource.editorPose

        with(canvas.graphicsContext2D) {
            save()
            translate(actorResource.x, actorResource.y)
            rotate(actorResource.direction.degrees - (pose?.direction?.degrees ?: 0.0))


            scale(actorResource.scale.x, actorResource.scale.y)

            if (ninePatch != null) {

                drawNinePatch(actorResource, ninePatch)

            } else {
                if (pose == null) {
                    actorResource.textStyle?.let {
                        drawText(it, actorResource.displayText)
                    }
                } else {
                    if (pose.tiled) {
                        drawTiled(actorResource, pose)
                    } else {
                        drawPose(pose)
                    }
                }
            }

            restore()
        }
    }

    fun drawNinePatch(actorResource: ActorResource, ninePatch: NinePatch) {

        val pose = ninePatch.pose
        val rect = pose.rect
        val size = actorResource.size

        val sourceLefts = listOf(0.0, ninePatch.left.toDouble(), (rect.width - ninePatch.right).toDouble())
        val sourceBottoms = listOf(0.0, ninePatch.bottom.toDouble(), (rect.width - ninePatch.top).toDouble())

        val sourceWidths = listOf(ninePatch.left.toDouble(), (rect.width - ninePatch.left - ninePatch.right).toDouble(), ninePatch.right.toDouble())
        val sourceHeights = listOf(ninePatch.bottom.toDouble(), (rect.height - ninePatch.top - ninePatch.bottom).toDouble(), ninePatch.top.toDouble())

        val destLefts = listOf(0.0, ninePatch.left.toDouble(), size.x - ninePatch.right)
        val destBottoms = listOf(0.0, ninePatch.bottom.toDouble(), size.y - ninePatch.top)

        val destWidths = listOf(ninePatch.left.toDouble(), actorResource.size.x - ninePatch.left - ninePatch.right, ninePatch.right.toDouble())
        val destHeights = listOf(ninePatch.bottom.toDouble(), actorResource.size.y - ninePatch.top - ninePatch.bottom, ninePatch.top.toDouble())


        with(canvas.graphicsContext2D) {
            save()
            translate(-actorResource.sizeAlignment.x * actorResource.size.x, -actorResource.sizeAlignment.y * actorResource.size.y)
            for (y in 0..2) {
                for (x in 0..2) {
                    if (sourceWidths[x] < 0.00001 || sourceHeights[y] < 0.00001) continue

                    drawImage(
                            pose.texture.cachedImage(),
                            pose.rect.left + sourceLefts[x], pose.rect.bottom - sourceBottoms[y], sourceWidths[x], -sourceHeights[y], // Source rect
                            destLefts[x], destBottoms[y], destWidths[x], destHeights[y]) // Dest rect
                }
            }
            restore()
        }
    }

    fun drawTiled(actorResource: ActorResource, pose: Pose) {
        with(canvas.graphicsContext2D) {
            save()
            translate(-actorResource.sizeAlignment.x * actorResource.size.x, -actorResource.sizeAlignment.y * actorResource.size.y)

            val across = Math.ceil(actorResource.size.x / pose.rect.width).toInt()
            val down = Math.ceil(actorResource.size.y / pose.rect.height).toInt()
            val w = actorResource.size.x / across
            val h = actorResource.size.y / down

            for (y in 0..down - 1) {
                for (x in 0..across - 1) {
                    drawImage(pose.texture.cachedImage(),
                            pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(),
                            x * w, y * h, w, h)
                }
            }

            restore()
        }
    }

    fun drawPose(pose: Pose) {
        val image = pose.texture.cachedImage()
        canvas.graphicsContext2D.drawImage(
                image,
                pose.rect.left.toDouble(), pose.rect.bottom.toDouble(), pose.rect.width.toDouble(), -pose.rect.height.toDouble(), // Source rect
                -pose.offsetX, -pose.offsetY, pose.rect.width.toDouble(), pose.rect.height.toDouble()) // Dest Rect
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
