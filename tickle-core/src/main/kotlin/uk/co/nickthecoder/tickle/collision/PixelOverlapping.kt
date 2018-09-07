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

/**
 * I use this during debugging, so that I can see the alpha channel for each of the buffer.
 * Please keep the "if (dump) ... code in place. It is really handy when things go wrong!
 */
private val dump = false

/**
 * Tests if any overlapping pixels have an alpha value greater than zero. For many images, this isn't very good,
 * because the anti-aliased / fuzzy edges of the image will make overlapping() return true, even when the objects
 * appear to be a pixel or two apart. Instead, consider using [ThresholdPixelOverlapping]. You can use the
 * [threshold] method of this class to create one.
 */
class PixelOverlapping(val size: Int = 128)

    : Overlapping {

    private val aFrameBufferId = glGenFramebuffersEXT()
    private val aTexture = Texture(size, size, GL_RGBA, null)

    private val bFrameBufferId = glGenFramebuffersEXT()
    private val bTexture = Texture(size, size, GL_RGBA, null)

    private val lineFrameBufferId = glGenFramebuffersEXT()
    private val lineTexture = Texture(size, 1, GL_RGBA, null)

    private val transparent = Color.white().transparent()

    private val projection = Matrix4f()

    init {
        println("*** GL Extensions available : " + glGetString(GL_EXTENSIONS))

        // Create a Texture, for each of the two actors
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, bTexture.handle, 0)
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, aFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, aTexture.handle, 0)

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

    /**
     * Finds the axis aligned rectangles (in world coordinates) for both actors.
     * If they don't intersect, we return false straight away.
     * Otherwise, render A and B to their own off-screen buffers.
     * Then use GL_MIN_EXT to render B's buffer onto A's buffer.
     * At this point, we could check the whole of A's buffer for any alpha channel values over the given threshold.
     * If there WERE high alpha channel values, then the actors overlap.
     *
     * However, an optimisation step is done...
     *
     * Take each line of A's buffer and render it using GL_MAX_EXT to ANOTHER buffer (which is only 1 pixel high)
     * Now, we only need to tests the alpha channel values for this much smaller, 1 pixel high buffer.
     *
     * Note, it's possible to perform a similar trick again, to create a single pixel, which holds the maximum
     * alpha channel. Is it worth it? I don't know, I haven't done performance tests.
     * The gains would be MUCH less though, so I haven't bothered to investigate further.
     */
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


            // Prepare to render B to a buffer, rather than the screen's frame buffer.
            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(left.toFloat(), right.toFloat(), bottom.toFloat(), top.toFloat())
            renderer.changeProjection(projection)
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bFrameBufferId)
            // Clear
            renderer.clearColor(transparent)
            renderer.clear()
            // Draw actor B "normally"
            renderer.begin()
            actorB.appearance.draw(renderer)
            renderer.end()

            // Stop rendering to the buffer. Many not needed?
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            glBindTexture(GL_TEXTURE_2D, 0)

            // Prepare to render A to a buffer, rather than the screen's frame buffer.
            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(left.toFloat(), right.toFloat(), bottom.toFloat(), top.toFloat())
            renderer.changeProjection(projection)
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, aFrameBufferId)
            // Clear
            renderer.clearColor(transparent)
            renderer.clear()
            // Draw actor A "normally"
            renderer.begin()
            actorA.appearance.draw(renderer)
            renderer.end()

            // We've now rendered A and B to their own buffer

            if (dump) {
                println("Actor B $actorB")
                bTexture.dumpAlpha()
                actorA.poseAppearance?.pose?.texture?.bind()

                println("Actor A $actorA")
                aTexture.dumpAlpha()
                actorA.poseAppearance?.pose?.texture?.bind()
            }

            // Stop rendering to the buffer. Many not needed?
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            glBindTexture(GL_TEXTURE_2D, 0)

            // Now merge the two using the MINIMUM values of each.
            // This will result in opaque pixels where they overlap, and transparent everywhere else.
            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(0f, width.toFloat(), 0f, height.toFloat())
            renderer.changeProjection(projection)

            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, aFrameBufferId)
            val bothSourceRect = Rectd()
            bothSourceRect.right = width.toDouble() / size
            bothSourceRect.top = height.toDouble() / size

            // Use GL_MIN_EXT, as we want transparent (low) alpha values wherever EITHER A or B are transparent.
            glBlendEquationEXT(GL_MIN_EXT)

            renderer.begin()
            renderer.drawTexture(bTexture, 0.0, 0.0, width.toDouble(), height.toDouble(), bothSourceRect)
            renderer.end()

            // Maybe remove these two?
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            glBlendEquationEXT(GL_FUNC_ADD_EXT)

            // Now we have opaque pixels where the actors overlap, and transparent everywhere else.
            if (dump) {
                println("Overlap")
                aTexture.dumpAlpha()
                actorA.poseAppearance?.pose?.texture?.bind()
            }

            // Render each line of the overlapTexture onto the lineTexture
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
                renderer.drawTexture(aTexture, 0.0, 0.0, width.toDouble(), 1.0, sourceRect)
            }
            renderer.end()
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            glBlendEquationEXT(GL_FUNC_ADD_EXT)

            // The lineTexture should now contain the result we need. Read the pixels, and compare with the threshold.
            if (dump) {
                //println("Line")
                //lineTexture.dumpAlpha()
                //println("")
                //println("left=$left right=$right top=$top bottom=$bottom width=$width height=$height")
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
