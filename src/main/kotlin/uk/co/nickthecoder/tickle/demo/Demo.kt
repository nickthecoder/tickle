package uk.co.nickthecoder.tickle.demo

import org.lwjgl.glfw.GLFW
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.stage.GameStage
import uk.co.nickthecoder.tickle.stage.Rectangle
import uk.co.nickthecoder.tickle.stage.ZOrderStageView

class Demo(
        window: Window,
        gameInfo: GameInfo,
        resources: Resources) : Game(window, gameInfo, resources) {

    val director : Director = Play()

    val stage = GameStage("main")
    val stageView = ZOrderStageView(Rectangle(0, 0, window.width, window.height), stage)

    val beeA = Actor(Bee())
    val coinA1 = Actor()
    val coinA2 = Actor()
    val grenadeA1 = Actor()
    val grenadeA2 = Actor()

    init {
        instance = this
    }

    override fun preInitialise() {
    }

    override fun postInitialise() {
        renderer.clearColor(Color(1.0f, 1.0f, 1.0f, 1.0f))

        window.enableVSync(1)
        window.keyboardEvents { onKey(it) }

        // The following code will be replaced by loading a scene from a json file when that is written.

        beeA.appearance = PoseAppearance(beeA, resources.beePose)
        grenadeA1.appearance = PoseAppearance(grenadeA1, resources.grenadePose)
        grenadeA2.appearance = PoseAppearance(grenadeA2, resources.grenadePose)
        coinA1.appearance = PoseAppearance(coinA1, resources.coinPose)
        coinA2.appearance = PoseAppearance(coinA2, resources.coinPose)

        coinA1.x = 100f
        coinA1.y = 100f

        coinA2.x = 300f
        coinA2.y = 300f

        grenadeA1.x = -50f
        grenadeA1.y = -100f

        grenadeA1.x = -150f
        grenadeA1.y = -100f

        stage.add(beeA)
        stage.add(coinA1)
        stage.add(coinA2)
        stage.add(grenadeA1)
        stage.add(grenadeA2)

        director.begin()
        stage.begin()
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
            stage.end()
            window.close()
        }
    }


    override fun tick() {
        director.preTick()
        stage.tick()
        director.postTick()

        with(renderer) {
            beginFrame()
            clear()
            stageView.draw(renderer)
            endFrame()
        }

        if (gameLoop.tickCount % 100 == 0L) {
            println("FPS = ${gameLoop.actualFPS()}")
        }

    }


    override fun postCleanup() {
        println("Demo ended cleanly")
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the game.
         */
        lateinit var instance: Demo
    }
}
