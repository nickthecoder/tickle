package uk.co.nickthecoder.tickle.util

data class Polar2f(val angle: Angle = Angle(), var magnitude: Float = 0f) {

    fun vector() = angle.vector().mul(magnitude)
}