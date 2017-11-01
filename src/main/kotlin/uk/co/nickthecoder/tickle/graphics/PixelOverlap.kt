package uk.co.nickthecoder.tickle.graphics

import org.joml.Matrix4f
import org.lwjgl.opengl.EXTBlendMinmax.*
import org.lwjgl.opengl.EXTFramebufferObject.*
import org.lwjgl.opengl.GL11.*
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.Rectd

/**
 * We compare the alpha channel of the result of overlapping the two images with the threshold. If any pixel is
 * above the threshold, then the Actors are considered to be touching.
 * A threshold of zero means they are overlapping if any pixels that are event slightly opaque overlap.
 * In practice, 0 is a bad default, but the "perfect" default value isn't obvious!
 */
class PixelOverlap(val threshold: Int = 16, val size: Int = 128) {

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

    fun overlapping(actorA: Actor, actorB: Actor): Boolean {

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

            // Render to the overlapTexture, rather than the regular frame buffer.
            // Render to our FBO
            glViewport(0, 0, width, height)
            projection.identity()
            projection.ortho2D(0f, width.toFloat(), 0f, height.toFloat())
            renderer.changeProjection(projection)
            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, overlapFrameBufferId)

            // Clear the overlap texture
            // TODO, If we are keeping a single large texture for all collisions, then we don't want to clear the whole buffer. We only need to clear the overlap area.
            renderer.clearColor(transparent)
            renderer.clear()

            // Draw actor A "normally" (The fragment shader will draw a WHITE pixels, but including the textures alpha (i.e. the rgb of the texture is ignored)
            renderer.begin()
            renderer.drawTexture(poseA.texture, worldA.left - left, worldA.bottom - bottom, worldA.right - left, worldA.top - bottom, poseA.rectd)
            renderer.end()

            // Draw actor B using logical AND (and still drawing WHITE pixels, but using the texture's alpha channel)
            glEnable(GL_COLOR_LOGIC_OP)
            glLogicOp(GL_AND)
            renderer.begin()
            renderer.drawTexture(poseB.texture, worldB.left - left, worldB.bottom - bottom, worldB.right - left, worldB.top - bottom, poseB.rectd)
            renderer.end()

            // Stop using logical AND, and return to normal blending
            glDisable(GL_COLOR_LOGIC_OP)

            // Now we have WHITE pixels where the actors overlap, and transparent everywhere else.
            //println("Overlap")
            //overlapTexture.dumpAlpha()

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
            //println("Line")
            //lineTexture.dumpAlpha()

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
