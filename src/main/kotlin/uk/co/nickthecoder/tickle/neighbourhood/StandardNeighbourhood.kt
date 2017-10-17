package uk.co.nickthecoder.tickle.neighbourhood

import java.util.*


class StandardNeighbourhood(
        override val blockWidth: Double,
        override val blockHeight: Double = blockWidth)

    : Neighbourhood {

    /**
     * The offset of the bottom-most row. Each NeighbourhoodRow has its own x-offset.
     */
    private var oy: Double = blockHeight / 2

    /**
     * The Blocks are arranged in a list of rows (each row has a list of Blocks).
     */
    private val rows = mutableListOf<NeighbourhoodRow>()


    override fun blocksAcross(): Int = rows.maxBy { it.count() }?.count() ?: 0

    override fun blocksDown(): Int = rows.size

    override fun clear() {
        rows.clear()
        oy = blockHeight / 2
    }

    override fun getBlock(x: Double, y: Double) = (getExistingRow(y) ?: createRow(y)).getBlock(x)

    override fun getExistingBlock(x: Double, y: Double): Block? = getExistingRow(y)?.getExistingBlock(x)


    /**
     * Finds the row for the given Y coordinate, or null if there is no row there.

     * @param y
     * *
     * @return The row if found, otherwise null
     */
    private fun getExistingRow(y: Double): NeighbourhoodRow? {
        val iy = Math.floor((y - oy) / blockHeight).toInt()
        if (iy < 0 || iy >= rows.size) {
            return null
        }
        return rows[iy]
    }

    private fun createRow(y: Double): NeighbourhoodRow {
        val iy = Math.floor((y - oy) / blockHeight).toInt()

        if (iy < 0) {

            val newRows = ArrayList<NeighbourhoodRow>(-iy)
            oy += iy * blockHeight
            for (i in 0..-iy - 1) {
                newRows.add(NeighbourhoodRow(oy + i * blockHeight))
            }
            rows.addAll(0, newRows)

        } else if (iy >= rows.size) {

            val extra = iy - rows.size + 1
            for (i in 0..extra - 1) {
                val row = NeighbourhoodRow(oy + rows.size * blockHeight)
                rows.add(row)
            }

        } else {
            throw RuntimeException("Attempt to recreate an existing row " + y)
        }

        return getExistingRow(y)!!
    }

    fun debug() {
        System.err.println("StandardNeighbourhood : " + blockWidth + "x" + blockHeight + " oy=" + oy)
        var y = oy
        for (row in rows) {
            System.err.println("\nRow : " + y + " ... " + row.y)

            var x = row.ox()
            for (sq in row.row()) {
                System.err.println("\n$sq : expected : $x,$y\n ")
                for (actor in sq.occupants) {
                    System.err.println(actor)
                }
                x += blockWidth
            }

            y += blockHeight
        }
    }

    private inner class NeighbourhoodRow(val y: Double) {

        private var ox: Double = blockWidth / 2

        private val row = mutableListOf<Block>()

        fun ox() = ox

        fun row(): List<Block> = row

        fun count() = row.size

        fun getBlock(x: Double): Block {
            var result = getExistingBlock(x)
            if (result == null) {
                result = createBlock(x)
            }
            return result

        }

        internal fun getExistingBlock(x: Double): Block? {
            val ix = Math.floor((x - ox) / blockWidth).toInt()
            if (ix < 0 || ix >= row.size) {
                return null
            }
            return row[ix]
        }

        private fun createBlock(x: Double): Block {
            val ix = Math.floor((x - ox) / blockWidth).toInt()

            if (ix < 0) {
                val newBlocks = ArrayList<Block>(-ix)
                ox += ix * this@StandardNeighbourhood.blockWidth
                for (i in 0..-ix - 1) {
                    newBlocks.add(Block(this@StandardNeighbourhood, ox + i * blockWidth, y))
                }
                row.addAll(0, newBlocks)
            } else {
                val extra = ix - row.size + 1
                for (i in 0..extra - 1) {
                    val block = Block(this@StandardNeighbourhood, ox + row.size * blockWidth, y)

                    row.add(block)
                }

            }

            return getExistingBlock(x)!!
        }

    }

}
