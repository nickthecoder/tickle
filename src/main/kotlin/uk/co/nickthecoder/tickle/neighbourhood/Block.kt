package uk.co.nickthecoder.tickle.neighbourhood

class Block(
        private val neighbourhood: Neighbourhood,
        val x: Double,
        val y: Double) {

    private var neighbours: MutableList<Block>? = null

    private val occupants = mutableSetOf<Occupant>()

    /**
     * This is only valid if you have previously called [initialiseNeighbours].
     */
    val neighbouringBlocks: List<Block>
        get() = this.neighbours!!


    /**
     * Caches the list of Blocks that touch this one, including diagonally, and also this Block. Therefore the length is
     * from 1 to 9.
     */
    fun initialiseNeighbours() {

        this.neighbours = ArrayList<Block>(9)

        for (dx in -1..1) {
            for (dy in -1..1) {

                if (dx == 0 && dy == 0) {
                    this.neighbours!!.add(this)

                    if (this.neighbourhood.getBlock(this.x, this.y) != this) {
                        throw RuntimeException("Block is in the wrong place")
                    }

                } else {
                    val neighbour = this.neighbourhood.getExistingBlock(
                            this.x + dx * this.neighbourhood.blockWidth,
                            this.y + dy * this.neighbourhood.blockHeight)

                    if (neighbour === this) {
                        throw RuntimeException("Block in two places at once. $dx,$dy")
                    }

                    if (neighbour != null) {

                        if (this.x != neighbour.x - dx * this.neighbourhood.blockWidth) {
                            throw RuntimeException("Incorrect x neighbour")
                        }

                        if (this.y != neighbour.y - dy * this.neighbourhood.blockHeight) {
                            throw RuntimeException("Incorrect y neighbour")
                        }

                        if (neighbour.neighbours != null) {
                            this.neighbours!!.add(neighbour)
                            neighbour.neighbours!!.add(this)
                        }
                    }
                }
            }
        }
    }

    fun add(occupant: Occupant) {
        this.occupants.add(occupant)
    }

    fun remove(occupant: Occupant) {
        this.occupants.remove(occupant)
    }

    fun getOccupants(): Set<Occupant> {
        return this.occupants
    }

    /**
     * Prints debugging info to stderr.
     */
    fun debug() {
        System.err.println("Debugging Block : " + this + "( " + this.x + "," + this.y + ")")
        System.err.println("Occupants : " + this.occupants)

        System.err.println()

        for (nb in this.neighbours!!) {
            System.err.println("Neighbour : " + nb)
            System.err.println("  Occupants : " + nb.occupants)
            System.err.println("  mutual neighbours : " + nb.neighbours!!.contains(this))
        }
    }

    override fun toString(): String {
        return "Block (" + this.x + "," + this.y + ")"
    }
}
