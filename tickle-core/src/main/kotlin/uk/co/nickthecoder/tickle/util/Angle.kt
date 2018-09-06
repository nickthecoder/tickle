/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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

    operator fun plus(other: Angle): Angle {
        return Angle.radians(radians + other.radians)
    }

    operator fun minus(other: Angle): Angle {
        return Angle.radians(radians - other.radians)
    }

    operator fun plusAssign(other: Angle) {
        radians += other.radians
    }

    operator fun minusAssign(other: Angle) {
        radians += other.radians
    }

    operator fun times(by: Double): Angle {
        return Angle.radians(radians * by)
    }

    operator fun timesAssign(by: Double) {
        radians *= by
    }

    operator fun div(by: Double): Angle {
        return Angle.radians(radians / by)
    }

    operator fun divAssign(by: Double) {
        radians /= by
    }


    override fun equals(other: Any?): Boolean {
        if (other is Angle) {
            return other.radians == radians
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return radians.hashCode() + 170
    }

    override fun toString() = "$degrees°"

    companion object {

        @JvmStatic fun degrees(degrees: Double) = Angle().apply { this.degrees = degrees }

        @JvmStatic fun radians(radians: Double) = Angle().apply { this.radians = radians }

        @JvmStatic fun of(vector: Vector2d) = Angle().apply { this.radians = radiansOf(vector) }

        @JvmStatic fun radiansOf(vector: Vector2d) = Math.atan2(vector.y, vector.x)

        @JvmStatic fun radiansOf(a: Vector2d, from: Vector2d) = Math.atan2(a.y - from.y, a.x - from.x)

        @JvmStatic fun of(a: Vector2d, from: Vector2d) = Math.atan2(a.y - from.y, a.x - from.x)
    }
}
