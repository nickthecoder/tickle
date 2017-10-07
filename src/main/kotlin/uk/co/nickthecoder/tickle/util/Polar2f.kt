package uk.co.nickthecoder.tickle.util

data class Polar2f(val angle: Angle = Angle(), val magnitude: Scalar = Scalar()) {

    fun vector() = angle.vector().mul(magnitude.value)
}