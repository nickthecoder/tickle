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

