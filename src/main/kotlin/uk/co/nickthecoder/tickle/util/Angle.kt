package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d

open class Angle() {

    open var radians: Double = 0.0

    var degrees: Double
        get() {
            return Math.toDegrees(radians)
        }
        set(v) {
            radians = Math.toRadians(v)
        }

    private val vector = Vector2d()

    fun vector(): Vector2d {
        vector.x = Math.cos(radians)
        vector.y = Math.sin(radians)
        return vector
    }

    fun of(vector2d: Vector2d) {
        radians = Math.atan2(vector2d.y, vector2d.x)
    }

    override fun toString() = "$degrees°"

    companion object {
        fun degrees(degrees: Double) = Angle().apply { this.degrees = degrees }

        fun radians(radians: Double) = Angle().apply { this.radians = radians }
    }
}
