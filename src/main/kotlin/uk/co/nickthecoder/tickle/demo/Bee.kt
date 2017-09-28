package uk.co.nickthecoder.tickle.demo


import uk.co.nickthecoder.tickle.action.DirectionControls

class Bee : Controllable() {

    override val movement = DirectionControls(
            maxSpeed = 10f,
            maxRotationSpeed = 5.0,
            rotationDrag = 0.07)

}
