package uk.co.nickthecoder.tickle.neighbourhood

import uk.co.nickthecoder.tickle.Role
import uk.co.nickthecoder.tickle.util.Rectd


class Occupant(
        private val neighbourhood: Neighbourhood, val role: Role) {

    private var blockRange = BlockRange(neighbourhood)

    fun update(worldRect: Rectd) {

        val bl = neighbourhood.getBlock(worldRect.left, worldRect.bottom)
        val tr = neighbourhood.getBlock(worldRect.right, worldRect.top)

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
            set.addAll(block.getOccupants())
        }
        set.remove(this)
        return set
    }
}
