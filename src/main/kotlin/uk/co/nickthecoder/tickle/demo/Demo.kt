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

    val bee = Bee()
    val hand = Hand()

    val handA = Actor(hand)
    val beeA = Actor(bee)
    val coinA1 = Actor(Coin(3f, 30.0, 3.0))
    val coinA2 = Actor(Coin(4f, 60.0, 2.0))

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
        handA.appearance = PoseAppearance(handA, resources.handPose)
        coinA1.appearance = PoseAppearance(coinA1, resources.coinPose)
        coinA2.appearance = PoseAppearance(coinA2, resources.coinPose)

        handA.x = -50f
        handA.y = 50f

        coinA1.x = -10f
        coinA1.y = 30f

        coinA2.x = 30f
        coinA2.y = -50f

        val count = 10
        for (i in 0..count - 1) {
            val grenadeA = Actor(Grenade(i.toFloat() / count))
            with(grenadeA) {
                appearance = PoseAppearance(grenadeA, resources.grenadePose)
                x = i * 150f - 200f
                y = -120f
            }
            stage.add(grenadeA, false)
        }


        stage.add(beeA, false)
        stage.add(handA, false)
        stage.add(coinA1, false)
        stage.add(coinA2, false)

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
            println("FPS = ${gameLoop.actualFPS().toInt()} Actors : ${stage.actors.size}")
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
