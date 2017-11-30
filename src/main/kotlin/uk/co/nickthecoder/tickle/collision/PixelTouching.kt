package uk.co.nickthecoder.tickle.collision

import org.joml.Matrix4f
import org.joml.Vector2d
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL11
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Texture

/**
 * Tests if one of the Actor's pixels is opaque.
 */
class PixelTouching(val threshold: Int = 0) {

    private val pixelFrameBufferId = EXTFramebufferObject.glGenFramebuffersEXT()
    private val pixelTexture = Texture(1, 1, GL11.GL_RGBA, null)

    private val transparent = Color.white().transparent()

    private val projection = Matrix4f()

    init {
        // Create a Texture, which holds the overlap of the the two actors
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, pixelFrameBufferId)
        EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, pixelTexture.handle, 0)

        // Don't use either of the frame buffers yet
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0)
    }

    /**
     * [point] is in Tickle's coordinates, i.e. (0,0) is the bottom left of the screen if the view has not been panned.
     */
    fun touching(actor: Actor, point: Vector2d): Boolean {
        val renderer = Game.instance.renderer

        // Begin writing to the pixel frame buffer rather than the screen's frame buffer.tt
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, pixelFrameBufferId)

        // Clear the pixel
        renderer.clearColor(transparent)
        renderer.clear()

        // Draw the actor onto the pixel frame buffer.
        GL11.glViewport(0, 0, 1, 1)
        projection.identity()
        projection.ortho2D(point.x.toFloat(), point.x.toFloat() + 1, point.y.toFloat(), point.y.toFloat() + 1)
        renderer.changeProjection(projection)

        renderer.beginView()
        renderer.begin()
        actor.appearance.draw(renderer)
        renderer.end()
        renderer.endView()

        // Stop using the pixel frame buffer, and return to rendering on the screen's frame buffer.
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0)

        pixelTexture.dumpAlpha()

        val pixels = pixelTexture.read()
        // Get the alpha channel byte of the first and only pixel. (i.e. pixels[3], which is the 4th byte)
        // and with 0xff to convert to unsigned.
        val pixel = pixels[3].toInt() and 0xff
        return pixel > threshold
    }

    companion object {
        /**
         * The default PixelTouching instance used by
         */
        var instance = PixelTouching()
    }

}
