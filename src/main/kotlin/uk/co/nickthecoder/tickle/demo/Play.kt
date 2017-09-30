package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.PoseAppearance
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.stage.GameStage
import uk.co.nickthecoder.tickle.stage.ZOrderStageView
import uk.co.nickthecoder.tickle.util.Recti

class Play : AbstractDirector() {

    val stage = GameStage()
    val stageView = ZOrderStageView()

    var degrees = 0.0

    val clockwise = Resources.instance.input("clockwise") // Z : Rotate the view
    val antiClockwise = Resources.instance.input("anti-clockwise") // X : Rotate the view
    val reset = Resources.instance.input("reset") // O : Reset the rotation to 0°
    val toggle = Resources.instance.input("toggle") // TAB : Toggles between Controllable roles.

    val centerOnControllable = CenterOnControllable()

    var activeControllable: Controllable? = null

    var centerAction: Action<Actor> = centerOnControllable
        set(v) {
            activeControllable?.let { v.begin(it.actor) }
            field = v
        }

    override fun begin() {
        println("Play begin")
        stageView.rect = Recti(0, 0, Game.instance.window.width, Game.instance.window.height)
        stageView.stage = stage

        // TODO Only here until loading scenes from a file is implemented.
        val bee = Bee()
        val hand = Hand()

        val handA = Actor(hand)
        val beeA = Actor(bee)
        val coinA1 = Actor(Coin(3f, 30.0, 3.0))
        val coinA2 = Actor(Coin(4f, 60.0, 2.0))

        beeA.appearance = PoseAppearance(beeA, Resources.instance.pose("bee"))
        handA.appearance = PoseAppearance(handA, Resources.instance.pose("hand"))
        coinA1.appearance = PoseAppearance(coinA1, Resources.instance.pose("coin"))
        coinA2.appearance = PoseAppearance(coinA2, Resources.instance.pose("coin"))

        beeA.directionRadians = beeA.appearance.directionRadians

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
                appearance = PoseAppearance(grenadeA, Resources.instance.pose("grenade"))
                x = i * 150f - 200f
                y = -120f
            }
            stage.add(grenadeA, false)
        }


        stage.add(beeA, false)
        stage.add(handA, false)
        stage.add(coinA1, false)
        stage.add(coinA2, false)

        println("Play Stage begin ${stage.actors.size}")
        stage.begin()
        activeControllable = tagManager.findARole(Tags.CONTROLLABLE) as Controllable
        activeControllable?.hasInput = true

    }

    override fun postTick() {
        stage.tick()

        if (reset.isPressed()) {
            degrees = 0.0
        }
        if (clockwise.isPressed()) {
            degrees -= 2
        }
        if (antiClockwise.isPressed()) {
            degrees += 2
        }

        activeControllable?.let { centerAction.act(it.actor) }
        stageView.degrees = degrees

        with(Game.instance.renderer) {
            beginFrame()
            clear()
            stageView.draw(this)
            endFrame()
        }

        if (Game.instance.gameLoop.tickCount % 100 == 0L) {
            println("FPS = ${Game.instance.gameLoop.actualFPS().toInt()} Actors : ${stage.actors.size}")
        }

    }

    override fun onKeyEvent(event: KeyEvent) {
        super.onKeyEvent(event)

        if (toggle.matches(event)) {

            tagManager.closest(activeControllable!!, Tags.CONTROLLABLE)?.let {

                if (it is Controllable) {
                    centerAction = WhizzToNextController(activeControllable!!)
                    activeControllable!!.hasInput = false
                    activeControllable = it
                } else {
                    System.err.println("ERROR. ${it} found to be CONTROLLABLE, but is not of type Controllable")
                }
            }
        }
    }

    inner class CenterOnControllable : Action<Actor> {
        override fun act(target: Actor): Boolean {
            stageView.centerX = target.x
            stageView.centerY = target.y

            return false
        }
    }

    inner class WhizzToNextController(oldControllable: Controllable) : AnimationAction<Actor>(0.5f, Eases.easeInOut) {

        var initialX = oldControllable.actor.x
        var initialY = oldControllable.actor.y

        override fun storeInitialValue(target: Actor) {
        }

        override fun act(target: Actor): Boolean {
            return super.act(target)
        }

        override fun update(target: Actor, t: Float) {
            stageView.centerX = lerp(initialX, target.x, t)
            stageView.centerY = lerp(initialY, target.y, t)
        }

        override fun ended() {
            centerAction = CenterOnControllable()
            activeControllable!!.hasInput = true
        }
    }

}
