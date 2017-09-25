package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Texture
import java.io.File

class Demo(gameInfo: GameInfo) : Game(gameInfo) {

    val imageDirectory = Game.resourceDirectory

    var coin: Texture? = null
    var grenade: Texture? = null

    var renderer: Renderer? = null

    override fun preInitialise() {
        println("Starting the Demo")
    }

    override fun postInitialise() {
        println("Demo postInitialise")
        // Set the clear color
        GL11.glClearColor(0.0f, 1.0f, 0.0f, 0.0f)

        window.enableVSync(1)
        window.keyboardEvents { onKey(it) }

        renderer = Renderer()
        coin = Texture.createTexture(File(imageDirectory, "coin.png"))
        grenade = Texture.createTexture(File(imageDirectory, "grenade.png"))
        println("Loaded coin : $coin")
    }

    fun onKey(event: KeyEvent) {
        if (event.key == GLFW.GLFW_KEY_ESCAPE && event.action == GLFW.GLFW_RELEASE) {
            println("Escape pressed")
            window.close()
        }
    }

    override fun tick() {
        //GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT) // clear the framebuffer

        renderer?.let { renderer ->
            renderer.clear()

            renderer.begin()
            renderer.drawTexture(coin!!, 10f, 10f)
            renderer.drawTexture(coin!!, 110f, 110f, Color.RED)
            renderer.end()
            renderer.begin()
            renderer.drawTexture(grenade!!, 10f, 250f)
            renderer.drawTexture(grenade!!, 80f, 250f, Color.SEMI_TRANSPARENT)
            renderer.end()
        }

        if (gameLoop.tickCount % 100 == 0L) {
            println("FPS = ${gameLoop.actualFPS()}")
        }
    }


    override fun postCleanup() {
        println("Demo ended cleanly")
    }
}
