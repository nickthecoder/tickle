package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.action.XYControls

class Hand : Controllable() {

    override val movement = XYControls(5f)

}
