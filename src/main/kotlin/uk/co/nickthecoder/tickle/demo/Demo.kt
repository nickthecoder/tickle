package uk.co.nickthecoder.tickle.demo

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Window

class Demo(
        window: Window,
        gameInfo: GameInfo,
        resources: Resources) : Game(window, gameInfo, resources) {

    var scrollX = 0f
    var scrollY = 0f

    override fun preInitialise() {
        println("Starting the Demo")
    }

    override fun postInitialise() {
        println("Demo postInitialise")

        renderer.clearColor(Color(1.0f, 1.0f, 1.0f, 1.0f))

        window.enableVSync(1)
        window.keyboardEvents { onKey(it) }
    }

    fun onKey(event: KeyEvent) {
        if (event.key == GLFW.GLFW_KEY_ESCAPE && event.action == GLFW.GLFW_RELEASE) {
            println("Escape pressed")
            window.close()
        }
        if (event.key == GLFW.GLFW_KEY_LEFT) {
            scrollX += 5
        }
        if (event.key == GLFW.GLFW_KEY_RIGHT) {
            scrollX -= 5
        }
    }

    override fun tick() {

        with(renderer) {
            moveView(scrollX, scrollY)
            frameStart()
            clear()

            drawTexture(resources.coin, 550f, 10f)
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
