package uk.co.nickthecoder.tickle.util

data class Polar2d(val angle: Angle = Angle(), var magnitude: Double = 0.0) {

    constructor(polar: Polar2d) : this(Angle.radians(polar.angle.radians), polar.magnitude)

    fun vector() = angle.vector().mul(magnitude)

    override fun toString() = "${angle.degrees}:${magnitude}"

    fun lerp(other: Polar2d, t: Double): Polar2d {
        return lerp(other, t, this)
    }

    fun lerp(other: Polar2d, t: Double, dest: Polar2d): Polar2d {
        dest.angle.radians = angle.radians * (1 - t) + other.angle.radians * t
        dest.magnitude = magnitude * (1 - t) + other.magnitude * t
        return dest
    }

    companion object {

        fun fromString(string: String): Polar2d {
            val split = string.split(":")
            return Polar2d(Angle.degrees(split[0].toDouble()), split[1].toDouble())
        }

    }
}
