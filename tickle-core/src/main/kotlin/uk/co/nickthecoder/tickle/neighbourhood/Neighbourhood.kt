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


/**
 * A rectangular grid of [Block]s, where each block can contain multiple occupants of type T.
 * T is often Role.
 */
interface Neighbourhood<T> {

    /**
     * Resets the Neighbourhood, so that there are no Actors held within it.
     * You can use this to reset the Neighbourhood at the beginning of a Scene, however, it is probably easier to create
     * a new Neighbourhood within your SceneDirector, that way, you will have a new Neighbourhood for each scene.
     */
    fun clear()

    /**
     * @return The width of the Blocks within this Neighbourhood.
     */
    val blockWidth: Double

    /**
     * @return The height of the Blocks within this Neighbourhood.
     */
    val blockHeight: Double

    /**
     * Looks for a Block within the neighbourhood. If a block at the given coordinates hasn't been
     * created yet, then that block is created.
     *
     *
     * Used when adding an Actor to the Neighbourhood.

     * @return The block at the given coordinate
     * *
     * @see .getExistingBlock
     */
    fun blockAt(x: Double, y: Double): Block<T>

    /**
     * Looks for a Block that has already been created.
     *
     *
     * Used when looking for an Actor. If there is no Block, then there is no need to create a new one.

     * @return The Block at the given coordinates, or null if there is no Block.
     * *
     * @see .getBlock
     */
    fun existingBlockAt(x: Double, y: Double): Block<T>?

    fun blocksAcross(): Int

    fun blocksDown(): Int

    fun width(): Double = blocksAcross() * blockWidth

    fun height(): Double = blocksDown() * blockHeight
}
