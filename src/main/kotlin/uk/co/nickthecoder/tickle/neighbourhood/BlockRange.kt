package uk.co.nickthecoder.tickle.neighbourhood


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
