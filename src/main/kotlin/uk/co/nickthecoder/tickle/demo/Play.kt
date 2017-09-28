package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.events.KeyEvent

class Play : Director {

    var degrees = 0.0

    val clockwise = Resources.instance.input("clockwise")
    val antiClockwise = Resources.instance.input("anti-clockwise")
    val reset = Resources.instance.input("reset")
    val toggle = Resources.instance.input("toggle")

    var oldActiveRole: Controlable = Demo.instance.bee
    var activeRole: Controlable = Demo.instance.bee

    override fun begin() {
        super.begin()
        activeRole.hasInput = true
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

        Demo.instance.stageView.centerX = activeRole.actor.x
        Demo.instance.stageView.centerY = activeRole.actor.y
        Demo.instance.stageView.degrees = degrees
    }

    override fun onKeyEvent(event: KeyEvent) {
        super.onKeyEvent(event)

        if (toggle.matches(event)) {
            println("Toggle")
            oldActiveRole = activeRole
            activeRole.hasInput = false
            activeRole = if (activeRole === Demo.instance.bee) Demo.instance.hand else Demo.instance.bee
            activeRole.hasInput = true
        }
    }

}
