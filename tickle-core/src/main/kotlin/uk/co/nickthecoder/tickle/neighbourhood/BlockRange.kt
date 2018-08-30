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
package uk.co.nickthecoder.tickle.neighbourhood


class BlockRange<T>(
        val neighbourhood: Neighbourhood<T>,
        var bottomLeft: Block<T>? = null,
        var topRight: Block<T>? = null)

    : Iterable<Block<T>> {

    override fun iterator(): Iterator<Block<T>> {

        val bottomLeft = this.bottomLeft
        val topRight = this.topRight

        if (bottomLeft == null || topRight == null) return emptyList<Block<T>>().iterator()

        return object : Iterator<Block<T>> {

            private var x = bottomLeft.x
            private var y = bottomLeft.y

            override fun hasNext(): Boolean {
                return y <= topRight.y
            }

            override fun next(): Block<T> {
                val block = neighbourhood.blockAt(x, y)
                x += neighbourhood.blockWidth
                if (x > topRight.x) {
                    y += neighbourhood.blockHeight
                    x = bottomLeft.x
                }
                return block
            }
        }
    }
}
