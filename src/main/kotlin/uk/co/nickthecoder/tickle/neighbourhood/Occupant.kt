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

class BlockRange(
        val neighbourhood: Neighbourhood,
        var bottomLeft: Block? = null,
        var topRight: Block? = null)

    : Iterable<Block> {

    override fun iterator(): Iterator<Block> {

        val bottomLeft = this.bottomLeft
        val topRight = this.topRight

        if (bottomLeft == null || topRight == null) return emptyList<Block>().iterator()

        return object : Iterator<Block> {

            private var x = bottomLeft.x
            private var y = bottomLeft.y

            override fun hasNext(): Boolean {
                return y <= topRight.y
            }

            override fun next(): Block {
                val block = neighbourhood.getBlock(x, y)
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
