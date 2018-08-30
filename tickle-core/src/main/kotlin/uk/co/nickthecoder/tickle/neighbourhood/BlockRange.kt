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
