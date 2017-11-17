package uk.co.nickthecoder.tickle.util

import org.joml.Vector2d

/**
 * A rectangle using Ints.
 * Note, the right and bottom values are EXCLUSIVE, so width = right - left
 *
 * The Y axis points up, so top > bottom
 *
 * For a rectangle with the y axis pointing down, use [YDownRect] instead.
 */
data class Recti(
        var left: Int,
        var bottom: Int,
        var right: Int,
        var top: Int) {

    val width
        get() = right - left
    val height
        get() = top - bottom

    fun contains(screenPosition: Vector2d): Boolean {
        return screenPosition.x >= left && screenPosition.x < right && screenPosition.y >= bottom && screenPosition.y < top
    }

    override fun toString(): String = "($left,$bottom , $right,$top)"
}
