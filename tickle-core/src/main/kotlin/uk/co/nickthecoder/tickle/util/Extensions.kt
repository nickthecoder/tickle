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

fun Vector2d.distanceSquared(other: Vector2d) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)

fun Vector2d.rotate(angle: Angle) = rotate(angle.radians)

fun Vector2d.rotate(radians: Double) {
    val sin = Math.sin(radians)
    val cos = Math.cos(radians)
    val rx = x * cos - y * sin
    val ry = y * cos + x * sin
    set(rx, ry)
}

fun Vector2d.radians() = Math.atan2(y, x)

fun Vector2d.degrees() = Math.toDegrees(Math.atan2(y, x))

fun Vector2d.string() = "( $x , $y )"


/**
 * Returns a list of all elements sorted according to the specified [comparator].
 */
fun <T> Iterable<T>.sortedBackwardsWith(comparator: Comparator<in T>): List<T> {
    if (this is Collection) {
        if (size <= 1) return this.toList()
        @Suppress("UNCHECKED_CAST")
        val array = (toTypedArray<Any?>() as Array<T>)
        array.sortWith(comparator)
        array.reverse()
        return array.asList()
    }
    return toMutableList().apply { sortedBackwardsWith(comparator) }
}
