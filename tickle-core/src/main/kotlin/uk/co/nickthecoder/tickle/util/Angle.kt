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

    override fun equals(other: Any?): Boolean {
        if (other is Angle) {
            return other.radians == radians
        } else {
            return false
        }
    }

    override fun toString() = "$degrees°"

    companion object {
        fun degrees(degrees: Double) = Angle().apply { this.degrees = degrees }

        fun radians(radians: Double) = Angle().apply { this.radians = radians }
    }
}
