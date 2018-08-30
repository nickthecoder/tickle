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

/**
 * A rectangle using Ints.
 * Note, the right and bottom values are EXCLUSIVE, so width = right - left
 *
 * The Y axis points up, so top > bottom
 *
 * For a rectangle with the y axis pointing down, use [YDownRect] instead.
 */
data class Recti(
        var left: Int,
        var bottom: Int,
        var right: Int,
        var top: Int) {

    val width
        get() = right - left
    val height
        get() = top - bottom

    fun contains(screenPosition: Vector2d): Boolean {
        return screenPosition.x >= left && screenPosition.x < right && screenPosition.y >= bottom && screenPosition.y < top
    }

    override fun toString(): String = "($left,$bottom , $right,$top)"
}
