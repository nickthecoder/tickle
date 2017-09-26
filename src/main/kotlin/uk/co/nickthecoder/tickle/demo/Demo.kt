package uk.co.nickthecoder.tickle.demo

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.math.Matrix4

class Demo(
        window: Window,
        gameInfo: GameInfo,
        resources: Resources) : Game(window, gameInfo, resources) {

    var rotationDegrees = 0.0

    val beeA = Actor()

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

        beeA.appearance = PoseAppearance(beeA, resources.beePose)
    }

    fun printProjection() {
        //println("Projection for $centerX, $centerY, $rotationDegrees")
        //println("Auth   : ${renderer.orthographicProjection(centerX, centerY)}")
        //println("zRot   : ${Matrix4.zRotation(centerX, centerY, toRadians(rotationDegrees))}")
        //println("Result : ${renderer.orthographicProjection(centerX, centerY) * Matrix4.zRotation(centerX, centerY, toRadians(rotationDegrees))}")
    }

    fun onKey(event: KeyEvent) {
        if (event.key == GLFW.GLFW_KEY_ESCAPE && event.action == GLFW.GLFW_RELEASE) {
            println("Escape pressed")
            window.close()
        }
        if (event.key == GLFW.GLFW_KEY_O) {
            beeA.x = 0f
            beeA.y = 0f
            rotationDegrees = 0.0
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_LEFT) {
            beeA.x -= 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_RIGHT) {
            beeA.x += 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_UP) {
            beeA.y += 5
            printProjection()
        }
        if (event.key == GLFW.GLFW_KEY_DOWN) {
            beeA.y -= 5
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

            rotateViewDegrees(beeA.x, beeA.y, rotationDegrees)

            frameStart()
            clear()

            beeA.appearance.draw(renderer)

            drawTexture(resources.coinTexture, 10f, 10f)
            drawTexture(resources.coinTexture, 110f, 110f, Color.RED)
            drawTexture(resources.grenadeTexture, 10f, 250f)
            drawTexture(resources.grenadeTexture, 80f, 250f, Color.SEMI_TRANSPARENT)

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
