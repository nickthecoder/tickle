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

class Block<T>(
        private val neighbourhood: Neighbourhood<T>,
        val x: Double,
        val y: Double) {

    private val _occupants = mutableSetOf<T>()

    val occupants: Set<T>
        get() = _occupants


    fun add(occupant: T) {
        _occupants.add(occupant)
    }

    fun remove(occupant: T) {
        _occupants.remove(occupant)
    }

    /**
     * Returns the block a given number of blocks away from this one ([dx],[dy])
     * Returns null if there is currently no block at that place.
     */
    fun neighbouringBlock(dx: Int, dy: Int): Block<T>? {
        return neighbourhood.existingBlockAt(x + dx * neighbourhood.blockWidth, y + dy * neighbourhood.blockHeight)
    }

    /**
     * Returns the block at the given offset ([dx],[dy]) away from this block (in world coordinates.
     * Returns null if there is currently no block at that place.
     */
    fun distantBlock(dx: Double, dy: Double): Block<T>? {
        return neighbourhood.existingBlockAt(x + dx, y + dy)
    }

    inline fun <reified S> hasInstance(): Boolean {
        for (occupant in occupants) {
            if (occupant is S) return true
        }
        return false
    }

    inline fun <reified S> findInstance(): S? {
        for (occupant in occupants) {
            if (occupant is S) return occupant
        }
        return null
    }

    inline fun <reified S> findInstances(): List<S> = occupants.filterIsInstance<S>()

    override fun toString(): String {
        return "Block (" + this.x + "," + this.y + ")"
    }
}

