package uk.co.nickthecoder.tickle.neighbourhood


/**
 * A rectangular grid of [Block]s.
 *
 *
 * Used by [Occupant] and [SinglePointCollisionStrategy] to speed up collision
 * detection, by only considering nearby Actors. SinglePointCollisionStrategy links to each Actor from only **one**
 * Block, whereas NeighbourhoodCollisionStrategy links to an Actor in all of the Blocks where the Actors bounding
 * rectangle overlaps the Block.
 */
interface Neighbourhood {

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
    fun getBlock(x: Double, y: Double): Block

    /**
     * Looks for a Block that has already been created.
     *
     *
     * Used when looking for an Actor. If there is no Block, then there is no need to create a new one.

     * @return The Block at the given coordinates, or null if there is no Block.
     * *
     * @see .getBlock
     */
    fun getExistingBlock(x: Double, y: Double): Block?

}
