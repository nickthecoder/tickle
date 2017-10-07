package uk.co.nickthecoder.tickle.util

import org.joml.Vector2f

open class Angle() {

    open var radians: Double = 0.0

    var degrees: Double
        get() {
            return Math.toDegrees(radians)
        }
        set(v) {
            radians = Math.toRadians(v)
        }

    private val vector = Vector2f()

    fun vector(): Vector2f {
        vector.x = Math.cos(radians).toFloat()
        vector.y = Math.sin(radians).toFloat()
        return vector
    }

    override fun toString() = "$degrees°"

    companion object {
        fun degrees(degrees: Double) = Angle().apply { this.degrees = degrees }

        fun radians(radians: Double) = Angle().apply { this.radians = radians }
    }
}
