package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.AnimationAction
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.events.KeyEvent

class Play : Director {

    var degrees = 0.0

    val clockwise = Resources.instance.input("clockwise")
    val antiClockwise = Resources.instance.input("anti-clockwise")
    val reset = Resources.instance.input("reset")
    val toggle = Resources.instance.input("toggle")

    val centerOnControllable = CenterOnControllable()

    var activeControllable: Controllable = Demo.instance.bee

    var centerAction: Action<Actor> = centerOnControllable
        set(v) {
            v.begin(activeControllable.actor)
            field = v
        }

    init {
        centerOnControllable.begin(activeControllable.actor)
    }

    override fun begin() {
        super.begin()
        activeControllable.hasInput = true
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

        centerAction.act(activeControllable.actor)
        Demo.instance.stageView.degrees = degrees
    }

    override fun onKeyEvent(event: KeyEvent) {
        super.onKeyEvent(event)

        if (toggle.matches(event)) {
            println("Toggle")
            centerAction = WhizzToNextController(activeControllable)
            activeControllable.hasInput = false
            activeControllable = if (activeControllable === Demo.instance.bee) Demo.instance.hand else Demo.instance.bee
        }
    }

    inner class CenterOnControllable : Action<Actor> {
        override fun act(target: Actor): Boolean {
            Demo.instance.stageView.centerX = target.x
            Demo.instance.stageView.centerY = target.y

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
            Demo.instance.stageView.centerX = lerp(initialX, target.x, t)
            Demo.instance.stageView.centerY = lerp(initialY, target.y, t)
        }

        override fun ended() {
            centerAction = CenterOnControllable()
            activeControllable.hasInput = true
        }
    }

}
