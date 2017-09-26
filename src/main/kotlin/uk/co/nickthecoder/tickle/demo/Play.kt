package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Resources

class Play : Director {

    var degrees = 0.0

    val clockwise = Resources.instance.input("clockwise")
    val antiClockwise = Resources.instance.input("anti-clockwise")
    val reset = Resources.instance.input("reset")

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

        Demo.instance.stageView.centerX = Demo.instance.beeA.x
        Demo.instance.stageView.centerY = Demo.instance.beeA.y
        Demo.instance.stageView.degrees = degrees
    }
}
