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
package uk.co.nickthecoder.tickle.collision

import org.joml.Matrix4f
import org.lwjgl.opengl.EXTBlendMinmax.*
import org.lwjgl.opengl.EXTFramebufferObject.*
import org.lwjgl.opengl.GL11.*
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Rectd

private val dump = false

/**
 * Tests if any overlapping pixels have an alpha value greater than zero. For many images, this isn't very good,
 * because the anti-aliased / fuzzy edges of the image will make overlapping() return true, even when the objects
 * appear to be a pixel or two apart. Instead, consider using [ThresholdPixelOverlapping]. You can use the
 * [threshold] method of this class to create one.
 */
class PixelOverlapping(val size: Int = 128)

    : Overlapping {

    private val overlapFrameBufferId = glGenFramebuffersEXT()
    private val overlapTexture = Texture(size, size, GL_RGBA, null)

    private val lineFrameBufferId = glGenFramebuffersEXT()
    private val lineTexture = Texture(size, 1, GL_RGBA, null)

    private val transparent = Color.white().transparent()

    private val projection = Matrix4f()

    init {
        // Create a Texture, which holds the overlap of the the two actors
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, overlapFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, overlapTexture.handle, 0)

        // Create a Texture, which is a 1 pixel high line
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, lineFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, lineTexture.handle, 0)

        // Don't use either of the frame buffers yet
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
    }

    fun threshold(threshold: Int) = ThresholdPixelOverlapping(threshold, this)

    override fun overlapping(actorA: Actor, actorB: Actor): Boolean {
        return overlapping(actorA, actorB, 0)
    }

    fun overlapping(actorA: Actor, actorB: Actor, threshold: Int): Boolean {

        val poseA = actorA.poseAppearance?.pose
        val poseB = actorB.poseAppearance?.pose
        if (poseA == null || poseB == null) {
            return false
        }

        val worldA = actorA.appearance.worldRect()
        val worldB = actorB.appearance.worldRect()

        if (worldA.right > worldB.left && worldA.top > worldB.bottom && worldA.left < worldB.right && worldA.bottom < worldB.top) {
            val renderer = Game.instance.renderer

            val left = Math.max(worldA.left, worldB.left)
            val bottom = Math.max(worldA.bottom, worldB.bottom)
            val right = Math.min(worldA.right, worldB.right)
            val top = Math.min(worldA.top, worldB.top)
            val width = (right - left).toInt()
            val height = (top - bottom).toInt()

            // Render to the overlapTexture, rather than the screen's frame buffer.
            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(left.toFloat(), right.toFloat(), bottom.toFloat(), top.toFloat())
            renderer.changeProjection(projection)
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, overlapFrameBufferId)

            // Clear the overlap texture
            // TODO, If we are keeping a single large texture for all collisions, then we don't want to clear the whole buffer. We only need to clear the overlap area.
            renderer.clearColor(transparent)
            renderer.clear()

            // Draw actor A "normally" (The fragment shader will draw a WHITE pixels, but including the textures alpha (i.e. the rgb of the texture is ignored)
            renderer.begin()
            actorA.appearance.draw(renderer)
            renderer.end()

            if (dump) {
                println("Actor A $actorA")
                overlapTexture.dumpAlpha()
                actorA.poseAppearance?.pose?.texture?.bind()
                glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, overlapFrameBufferId)
            }

            // Use MIN blending, as we only care about the least opaque of the two pixelso
            glBlendEquationEXT(GL_MIN_EXT)

            // Draw actor B using logical AND (and still drawing WHITE pixels, but using the texture's alpha channel)
            //glEnable(GL_COLOR_LOGIC_OP)
            //glLogicOp(GL_AND)

            renderer.begin()
            actorB.appearance.draw(renderer)
            renderer.end()

            // Stop using logical AND, and return to normal blending
            //glDisable(GL_COLOR_LOGIC_OP)

            // Now we have WHITE pixels where the actors overlap, and transparent everywhere else.
            if (dump) {
                println("Overlap")
                overlapTexture.dumpAlpha()
            }

            // Blit each line of the overlapTexture onto the lineTexture
            // By doing so, we reduce the amount of data transfered from GPU to main memory
            glViewport(0, 0, width, 1)
            projection.identity()
            projection.ortho2D(0f, width.toFloat(), 0f, 1f)
            renderer.changeProjection(projection)
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, lineFrameBufferId)

            // Use MAX blending, as we only care about the most opaque overlapping pixels
            glBlendEquationEXT(GL_MAX_EXT)

            renderer.clear()
            renderer.begin()
            val sourceRect = Rectd()
            sourceRect.right = width.toDouble() / size
            val ratio = 1.0 / size
            for (y in 0..height - 1) {
                //for (y in 10..10) {
                sourceRect.bottom = ratio * y
                sourceRect.top = ratio * (y + 1)
                renderer.drawTexture(overlapTexture, 0.0, 0.0, width.toDouble(), 1.0, sourceRect)
            }
            renderer.end()
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            glBlendEquationEXT(GL_FUNC_ADD_EXT)

            // The lineTexture should now contain the result we need. Read the pixels, and compare with the threshold.
            if (dump) {
                println("Line")
                lineTexture.dumpAlpha()
            }

            val pixels = lineTexture.read()
            for (x in 0..width - 1) {
                val pixel = pixels[x * 4 + 3].toInt() and 0xff
                if (pixel > threshold) {
                    return true
                }
            }
        }

        return false
    }

}
