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
 * A rectangle using Ints.
 * Note, the right and bottom values are EXCLUSIVE, so width = right - left
 *
 * As this is used for rectangles within an image, it sticks with the normal convention of the y axis pointing downwards
 * i.e. bottom > top.
 *
 * For a rectangle with the y axis pointing up, use [Recti] instead.
 */
data class YDownRect(
        var left: Int,
        var top: Int,
        var right: Int,
        var bottom: Int) {

    val width
        get() = right - left
    val height
        get() = bottom - top

    fun contains(x: Int, y: Int): Boolean {
        if (x < left || x > right) return false
        return y >= top && y <= bottom
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YDownRect) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left,$top , $right,$bottom)"
}
