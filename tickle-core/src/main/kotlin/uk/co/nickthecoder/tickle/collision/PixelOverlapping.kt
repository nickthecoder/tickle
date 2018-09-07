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
 * Tests if any overlapping pixels have an alpha value greater than the given threshold.
 *
 * [size] : The maximum height and width of the intermediate images created which checking for overlap.
 * Generally, this should be a bit larger than the smaller of the two images being tested.
 * So if you are checking is a HUGE image overlaps a smaller one, then [size] is only dependant on the smaller size.
 * Note. When actors are rotated, then [size] needs to be bigger actor's image (by a factor of about 1.5)
 *
 * [threshold] : In the range 0..255. Determines how opaque/transparent a pixel is before treating it as overlapping.
 * A value of 0 tests for fully opaque pixels (this isn't very good for images with fuzzy or anti-aliased edges).
 * The default of 128 is a reasonable value.
 *
 * Typically, a single instance of PixelOverlapping is created in your game's Director.
 * Do NOT create a new instance every time you want to test for collisions!
 */
open class PixelOverlapping(val size: Int, val threshold: Int = 128)

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
        if (!glGetString(GL_EXTENSIONS).contains("GL_EXT_blend_minmax")) {
            System.err.println("WARNING : GL_EXT_blend_minmax is not supported by your graphics card driver. PixelOverlapping will not work.")
        }

        // Create a Texture, for each of the two actors
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, aFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, aTexture.handle, 0)
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, bTexture.handle, 0)

        // Create a Texture, which is a 1 pixel high line (for the optimisation code)
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, lineFrameBufferId)
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, lineTexture.handle, 0)

        // Don't use either of the frame buffers yet
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
    }

    /*
    protected fun finalize() {
        glDeleteFramebuffersEXT(aFrameBufferId)
        glDeleteFramebuffersEXT(bFrameBufferId)
        glDeleteFramebuffersEXT(lineFrameBufferId)
    }
    */

    override fun overlapping(actorA: Actor, actorB: Actor): Boolean {
        return overlapping(actorA, actorB, threshold)
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

        val worldA = actorA.appearance.worldRect()
        val worldB = actorB.appearance.worldRect()

        // If the axis aligned rectangles don't intersect, then we can return true without testing pixels.
        if (worldA.right > worldB.left && worldA.top > worldB.bottom && worldA.left < worldB.right && worldA.bottom < worldB.top) {
            val renderer = Game.instance.renderer

            val left = Math.max(worldA.left, worldB.left)
            val bottom = Math.max(worldA.bottom, worldB.bottom)
            val right = Math.min(worldA.right, worldB.right)
            val top = Math.min(worldA.top, worldB.top)
            val width = (right - left).toInt()
            val height = (top - bottom).toInt()


            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(left.toFloat(), right.toFloat(), bottom.toFloat(), top.toFloat())
            renderer.changeProjection(projection)
            renderer.clearColor(transparent)

            // TODO The order which we render A and B matters is seems.
            // This is surely an indication of a bug, but I've run out of time for now,
            // and the bug seem minor in the test cases I've created.

            // Render B to a buffer "normally", rather than the screen's frame buffer.
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bFrameBufferId)
            renderer.clear()
            renderer.begin()
            actorB.appearance.draw(renderer)
            renderer.end()

            // Render A to a buffer "normally"
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, aFrameBufferId)
            renderer.clear()
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

            // Now merge the two using the MINIMUM values of each.
            // This will result in opaque pixels where they overlap, and transparent everywhere else.
            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(0f, width.toFloat(), 0f, height.toFloat())
            renderer.changeProjection(projection)

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, aFrameBufferId)
            val bSourceRect = Rectd()
            bSourceRect.right = width.toDouble() / size
            bSourceRect.top = height.toDouble() / size

            // Use GL_MIN_EXT, as we want transparent (low) alpha values wherever EITHER A or B are transparent.
            glBlendEquationEXT(GL_MIN_EXT)

            renderer.begin()
            renderer.drawTexture(bTexture, 0.0, 0.0, width.toDouble(), height.toDouble(), bSourceRect)
            renderer.end()

            // Now we should have opaque pixels where the actors overlap, and transparent everywhere else.
            if (dump) {
                println("Overlap")
                aTexture.dumpAlpha()
                actorA.poseAppearance?.pose?.texture?.bind()
            }

            // The following is an optimisation, so that we don't need to test EVERY pixel of aTexture.

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
                sourceRect.bottom = ratio * y
                sourceRect.top = ratio * (y + 1)
                renderer.drawTexture(aTexture, 0.0, 0.0, width.toDouble(), 1.0, sourceRect)
            }
            renderer.end()
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            glBlendEquationEXT(GL_FUNC_ADD_EXT)

            // The lineTexture should now contain the result we need. Read the pixels, and compare with the threshold.
            if (dump) {
                println("Line")
                lineTexture.dumpAlpha()
                println("")
                println("left=$left right=$right top=$top bottom=$bottom width=$width height=$height")
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
