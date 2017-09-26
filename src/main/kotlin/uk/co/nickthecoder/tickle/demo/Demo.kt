package uk.co.nickthecoder.tickle.demo

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.GameInfo
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.math.Matrix4
import uk.co.nickthecoder.tickle.math.toRadians

class Demo(
        window: Window,
        gameInfo: GameInfo,
        resources: Resources) : Game(window, gameInfo, resources) {

    var centerX = 0f
    var centerY = 0f
    var rotationDegrees = 0.0

    override fun preInitialise() {
        println("Starting the Demo")
        println("Render other proj = ${renderer.orthographicProjection(0f, 0f)}")
        println(" * identity       = ${renderer.orthographicProjection(0f, 0f) * Matrix4()}")
        println("Rotate 0          = ${Matrix4.zRotation(0f, 0f, 0.0)}")
        println("Rotate about 0,0= ${Matrix4.zRotation(0f, 0f, Math.PI / 2)}")
        println("Rotate about 5,7= ${Matrix4.zRotation(5f, 7f, Math.PI / 2)}")
    }

    override fun postInitialise() {
        println("Demo postInitialise")

        renderer.clearColor(Color(1.0f, 1.0f, 1.0f, 1.0f))

        window.enableVSync(1)
        window.keyboardEvents { onKey(it) }
    }

    fun printProjection() {
        println("Projection for $centerX, $centerY, $rotationDegrees")
        println("Auth   : ${renderer.orthographicProjection(centerX, centerY)}")
        println("zRot   : ${Matrix4.zRotation(centerX, centerY, toRadians(rotationDegrees))}")
        println("Result : ${renderer.orthographicProjection(centerX, centerY) * Matrix4.zRotation(centerX, centerY, toRadians(rotationDegrees))}")
    }

    fun onKey(event: KeyEvent) {
        if (event.key == GLFW.GLFW_KEY_ESCAPE && event.action == GLFW.GLFW_RELEASE) {
            println("Escape pressed")
            window.close()
        }
        if (event.key == GLFW.GLFW_KEY_O) {
            centerX = 0f
            centerY = 0f
            rotationDegrees = 0.0
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_LEFT) {
            centerX -= 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_RIGHT) {
            centerX += 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_UP) {
            centerY += 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_DOWN) {
            centerY -= 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_Z) {
            rotationDegrees += 3
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_X) {
            rotationDegrees -= 3
            printProjection()
        }
    }

    override fun tick() {

        with(renderer) {

            rotateViewDegrees(centerX, centerY, rotationDegrees)

            frameStart()
            clear()

            drawTexture(resources.coin, centerX - 30, centerY - 30)
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
