package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.stage.Stage
import uk.co.nickthecoder.tickle.stage.ZOrderStageView

class Play : AbstractDirector() {

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


    lateinit var stage: Stage
    lateinit var stageView: ZOrderStageView

    override fun begin() {
        stage = Game.instance.scene.findStage("main")!!
        stageView = Game.instance.scene.findStageView("main")!! as ZOrderStageView
        println("Play begin")
    }

    override fun activated() {
        activeControllable = tagManager.findARole(Tags.CONTROLLABLE) as Controllable?
        activeControllable?.hasInput = true
        println("Play activated")
    }

    override fun postTick() {
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
