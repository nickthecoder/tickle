package uk.co.nickthecoder.tickle

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Renderer
import uk.co.nickthecoder.tickle.graphics.Window

class Demo(window: Window, gameInfo: GameInfo, resources: Resources) : Game(window, gameInfo, resources) {

    var renderer = Renderer()

    override fun preInitialise() {
        println("Starting the Demo")
    }

    override fun postInitialise() {
        println("Demo postInitialise")

        renderer.clearColor( Color(1.0f, 1.0f, 1.0f, 1.0f))

        window.enableVSync(1)
        window.keyboardEvents { onKey(it) }
    }

    fun onKey(event: KeyEvent) {
        if (event.key == GLFW.GLFW_KEY_ESCAPE && event.action == GLFW.GLFW_RELEASE) {
            println("Escape pressed")
            window.close()
        }
    }

    override fun tick() {

        with(renderer) {
            frameStart()
            clear()

            drawTexture(resources.coin, 10f, 10f)
            drawTexture(resources.coin, 110f, 110f, Color.RED)
            drawTexture(resources.grenade, 10f, 250f)
            drawTexture(resources.grenade, 80f, 250f, Color.SEMI_TRANSPARENT)

            frameEnd()
        }

        if (gameLoop.tickCount % 100 == 0L) {
            println("FPS = ${gameLoop.actualFPS()}")
        }
    }


    override fun postCleanup() {
        println("Demo ended cleanly")
    }
}
