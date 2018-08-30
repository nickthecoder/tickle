package uk.co.nickthecoder.tickle.sandbox

import org.joml.Matrix4f
import org.lwjgl.opengl.EXTFramebufferObject.*
import org.lwjgl.opengl.GL11.*
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Texture
import uk.co.nickthecoder.tickle.util.Rectd
import java.io.File

/**
 * Not finished!
 * Moves two items over each other, and at the same time, shows the intermediate results of pixel based overlap detection.
 */
class OverlapSandbox() : Sandbox() {

    val coinT = Texture.create(File("src/dist/resources/images/coin.png"))
    val soilT = Texture.create(File("src/dist/resources/images/soil.png"))

    var coinX = 30.0
    var coinY = 0.0

    var soilX = 100.0
    var soilY = 30.0

    val unitRect = Rectd(0.0, 0.0, 1.0, 1.0)
    val coinWorldRect = Rectd()
    val soilWorldRect = Rectd()


    var frameBufferID: Int = 0
    val size = 128
    val overlapTexture = Texture(size, size, GL_RGBA, null)


    init {
        frameBufferID = glGenFramebuffersEXT()

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferID)

        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, overlapTexture.handle, 0)
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
    }

    val fboProjection = Matrix4f()

    override fun tick() {
        renderer.beginView()
        renderer.clearColor(Color.black())
        renderer.clear()

        coinX++

        coinWorldRect.left = coinX
        coinWorldRect.bottom = coinY
        coinWorldRect.right = coinX + coinT.width
        coinWorldRect.top = coinY + coinT.height

        soilWorldRect.left = soilX
        soilWorldRect.bottom = soilY
        soilWorldRect.right = soilX + soilT.width
        soilWorldRect.top = soilY + soilT.height

        renderer.drawTexture(coinT, coinWorldRect, unitRect)
        renderer.drawTexture(soilT, soilWorldRect, unitRect)

        renderer.end()

        if (soilWorldRect.right > coinWorldRect.left && soilWorldRect.top > coinWorldRect.bottom && soilWorldRect.left < coinWorldRect.right && soilWorldRect.bottom < coinWorldRect.top) {
            println("Overlapping rects")
            val left = Math.max(soilWorldRect.left, coinWorldRect.left)
            val bottom = Math.max(soilWorldRect.bottom, coinWorldRect.bottom)
            val right = Math.min(soilWorldRect.right, coinWorldRect.right)
            val top = Math.min(soilWorldRect.top, coinWorldRect.top)

            // Render to our FBO
            glViewport(0, 0, (right - left).toInt(), (top - bottom).toInt())
            fboProjection.identity()
            fboProjection.ortho2D(0f, (right - left).toFloat(), 0f, (top - bottom).toFloat())
            renderer.changeProjection(fboProjection)
            println("Projection = $fboProjection")

            glBindTexture(GL_TEXTURE_2D, 0)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBufferID)

            renderer.clearColor(Color.white().transparent())
            renderer.clear()


            renderer.begin()
            renderer.drawTexture(coinT, coinWorldRect.left - left, coinWorldRect.bottom - bottom, coinWorldRect.right - left, coinWorldRect.top - bottom, unitRect)
            renderer.end()

            glEnable(GL_COLOR_LOGIC_OP)
            glLogicOp(GL_AND)

            renderer.begin()
            renderer.drawTexture(soilT, soilWorldRect.left - left, soilWorldRect.bottom - bottom, soilWorldRect.right - left, soilWorldRect.top - bottom, unitRect)
            renderer.end()
            glDisable(GL_COLOR_LOGIC_OP)

            println("Alpha channel dump\n")
            overlapTexture.dumpAlpha()
            println("\n")

            // Render on the window again
            glViewport(0, 0, window.width, window.height)
            renderer.changeProjection(projection)
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
            renderer.begin()
            renderer.drawTexture(overlapTexture, 150.0, 200.0, 150.0 + size, 200.0 + size, unitRect)
            renderer.end()
        }

        renderer.endView()
        window.swap()
    }

}

fun main(args: Array<String>) {
    OverlapSandbox().loop()
}
