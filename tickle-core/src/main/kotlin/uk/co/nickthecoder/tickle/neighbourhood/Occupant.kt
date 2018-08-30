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

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.util.Rectd


class Occupant(
        private val neighbourhood: Neighbourhood<Occupant>, val role: Role) {

    private var blockRange = BlockRange(neighbourhood)

    fun update(worldRect: Rectd) {

        val bl = neighbourhood.blockAt(worldRect.left, worldRect.bottom)
        val tr = neighbourhood.blockAt(worldRect.right, worldRect.top)

        if (blockRange.bottomLeft !== bl || blockRange.topRight !== tr) {
            remove()
            blockRange.bottomLeft = bl
            blockRange.topRight = tr
            blockRange.forEach { it.add(this) }
        }
    }

    /**
     * Removes this Occupant from all of the Block it previously occupied.
     */
    fun remove() {
        blockRange.forEach { it.remove(this) }
    }

    fun neighbours(): Set<Occupant> {
        val set = mutableSetOf<Occupant>()
        blockRange.forEach { block ->
            set.addAll(block.occupants)
        }
        set.remove(this)
        return set
    }
}
