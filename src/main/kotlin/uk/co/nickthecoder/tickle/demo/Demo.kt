package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.PoseAppearance
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.stage.GameStage
import uk.co.nickthecoder.tickle.stage.Rectangle
import uk.co.nickthecoder.tickle.stage.ZOrderStageView

class Demo(
        window: Window,
        resources: Resources) : Game(window, resources) {

    val stage = GameStage("main")
    val stageView = ZOrderStageView(Rectangle(0, 0, window.width, window.height), stage)

    val beeA = Actor(Bee())
    val coinA1 = Actor(Rotating(3f, 30.0, 3.0))
    val coinA2 = Actor(Rotating(4f, 60.0, 2.0))

    override fun preInitialise() {
        instance = this
    }

    override fun postInitialise() {
        renderer.clearColor(Color(1.0f, 1.0f, 1.0f, 1.0f))

        window.enableVSync(1)
        window.keyboardEvents { onKeyEvent(it) }

        // The following code will be replaced by loading a scene from a json file when that is written.

        director = Play()

        beeA.appearance = PoseAppearance(beeA, resources.beePose)
        coinA1.appearance = PoseAppearance(coinA1, resources.coinPose)
        coinA2.appearance = PoseAppearance(coinA2, resources.coinPose)

        coinA1.x = -10f
        coinA1.y = 30f

        coinA2.x = 30f
        coinA2.y = -50f


        stage.add(beeA)
        stage.add(coinA1)
        stage.add(coinA2)

        for (i in 0..10) {
            val grenadeA = Actor(Grenade())
            with(grenadeA) {
                appearance = PoseAppearance(grenadeA, resources.grenadePose)
                x = i * 100f - 200f
                y = -120f
            }
            stage.add(grenadeA)
        }

        director.begin()
        stage.begin()
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
