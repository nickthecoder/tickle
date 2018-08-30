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

/**
 * A rectangle using Double
 */
data class Rectd(
        var left: Double,
        var bottom: Double,
        var right: Double,
        var top: Double) {

    val width
        get() = right - left
    val height
        get() = top - bottom

    constructor() : this(0.0, 0.0, 0.0, 0.0)

    constructor(other: Rectd) : this(other.left, other.bottom, other.right, other.top)

    fun plus(dx: Double, dy: Double, dest: Rectd = this): Rectd {
        dest.left = left + dx
        dest.right = right + dx
        dest.top = top + dy
        dest.bottom = bottom + dy
        return dest
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rectd) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left , $bottom , $right , $top)"
}
