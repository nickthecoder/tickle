package uk.co.nickthecoder.tickle.util

/**
 * A rectangle using Ints
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
    val topDownHeight
        get() = bottom - top

    override fun equals(other: Any?): Boolean {
        if (other !is Recti) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left,$bottom , $right,$top)"
}
