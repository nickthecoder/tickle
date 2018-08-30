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

import java.util.*


class StandardNeighbourhood<T>(
        override val blockWidth: Double,
        override val blockHeight: Double = blockWidth)

    : Neighbourhood<T> {

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

    override fun blockAt(x: Double, y: Double) = (existingRowAt(y) ?: createRow(y)).blockAt(x)

    override fun existingBlockAt(x: Double, y: Double): Block<T>? = existingRowAt(y)?.existingBlockAt(x)


    /**
     * Finds the row for the given Y coordinate, or null if there is no row there.

     * @param y
     * *
     * @return The row if found, otherwise null
     */
    private fun existingRowAt(y: Double): NeighbourhoodRow? {
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

        return existingRowAt(y)!!
    }

    fun debug() {
        System.err.println("StandardNeighbourhood : " + blockWidth + "x" + blockHeight + " oy=" + oy)
        var y = oy
        for (row in rows) {
            System.err.println("\n\nRow : " + y + " ... " + row.y)

            var x = row.ox()
            for (block in row.row()) {
                System.err.println("$block : expected : $x , $y ${block.occupants}")
                x += blockWidth
            }

            y += blockHeight
        }
    }

    private inner class NeighbourhoodRow(val y: Double) {

        private var ox: Double = blockWidth / 2

        private val row = mutableListOf<Block<T>>()

        fun ox() = ox

        fun row(): List<Block<T>> = row

        fun count() = row.size

        fun blockAt(x: Double): Block<T> {
            var result = existingBlockAt(x)
            if (result == null) {
                result = createBlock(x)
            }
            return result

        }

        internal fun existingBlockAt(x: Double): Block<T>? {
            val ix = Math.floor((x - ox) / blockWidth).toInt()
            if (ix < 0 || ix >= row.size) {
                return null
            }
            return row[ix]
        }

        private fun createBlock(x: Double): Block<T> {
            val ix = Math.floor((x - ox) / blockWidth).toInt()

            if (ix < 0) {
                val newBlocks = ArrayList<Block<T>>(-ix)
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

            return existingBlockAt(x)!!
        }

    }

}
