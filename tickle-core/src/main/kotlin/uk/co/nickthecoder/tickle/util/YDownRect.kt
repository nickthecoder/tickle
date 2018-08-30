package uk.co.nickthecoder.tickle.util

/**
 * A rectangle using Ints.
 * Note, the right and bottom values are EXCLUSIVE, so width = right - left
 *
 * As this is used for rectangles within an image, it sticks with the normal convention of the y axis pointing downwards
 * i.e. bottom > top.
 *
 * For a rectangle with the y axis pointing up, use [Recti] instead.
 */
data class YDownRect(
        var left: Int,
        var top: Int,
        var right: Int,
        var bottom: Int) {

    val width
        get() = right - left
    val height
        get() = bottom - top

    fun contains(x: Int, y: Int): Boolean {
        if (x < left || x > right) return false
        return y >= top && y <= bottom
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YDownRect) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left,$top , $right,$bottom)"
}
