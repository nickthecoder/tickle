package uk.co.nickthecoder.tickle.util

/**
 * A rectangle using floats
 */
data class Rectf(
        var left: Float,
        var bottom: Float,
        var right: Float,
        var top: Float) {

    val width
        get() = right - left
    val height
        get() = top - bottom

    override fun equals(other: Any?): Boolean {
        if (other !is Rectf) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left , $bottom   ,   $right , $top)"
}
