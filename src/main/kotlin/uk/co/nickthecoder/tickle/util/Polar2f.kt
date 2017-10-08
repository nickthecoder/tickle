package uk.co.nickthecoder.tickle.util

data class Polar2f(val angle: Angle = Angle(), var magnitude: Float = 0f) {

    constructor(polar: Polar2f) : this(Angle.radians(polar.angle.radians), polar.magnitude)

    fun vector() = angle.vector().mul(magnitude)

    override fun toString() = "${angle.degrees}:${magnitude}"

    fun lerp(other: Polar2f, t: Float): Polar2f {
        return lerp(other, t, this)
    }

    fun lerp(other: Polar2f, t: Float, dest: Polar2f): Polar2f {
        dest.angle.radians = angle.radians * (1 - t) + other.angle.radians * t
        dest.magnitude = magnitude * (1 - t) + other.magnitude * t
        return dest
    }

    companion object {

        fun fromString(string: String): Polar2f {
            val split = string.split(":")
            return Polar2f(Angle.degrees(split[0].toDouble()), split[1].toFloat())
        }

    }
}
