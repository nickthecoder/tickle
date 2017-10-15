package uk.co.nickthecoder.tickle.neighbourhood

import uk.co.nickthecoder.tickle.Role

class Block(
        private val neighbourhood: Neighbourhood,
        val x: Double,
        val y: Double) {

    private val _occupants = mutableSetOf<Occupant>()

    val occupants : Set<Occupant>
        get() = _occupants


    fun add(role: Role): Occupant {
        val occupant = Occupant(neighbourhood, role)
        add(occupant)
        return occupant
    }

    fun remove(role: Role) {
        _occupants.firstOrNull { it.role === role }?.let { remove(it) }
    }

    fun add(occupant: Occupant) {
        _occupants.add(occupant)
    }

    fun remove(occupant: Occupant) {
        _occupants.remove(occupant)
    }

    fun neighbouringBlock(dx: Int, dy: Int): Block? {
        return neighbourhood.getExistingBlock(x + dx * neighbourhood.blockWidth, y + dy * neighbourhood.blockHeight)
    }

    fun distantBlock(dx: Double, dy: Double): Block? {
        return neighbourhood.getExistingBlock(x + dx, y + dy)
    }

    override fun toString(): String {
        return "Block (" + this.x + "," + this.y + ")"
    }
}
