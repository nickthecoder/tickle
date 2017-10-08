package uk.co.nickthecoder.tickle.util

/**
 * A rectangle using Double
 */
data class Rectd(
        var left: Double,
        var bottom: Double,
        var right: Double,
        var top: Double) {

    val width
        get() = right - left
    val height
        get() = top - bottom

    override fun equals(other: Any?): Boolean {
        if (other !is Rectd) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left , $bottom   ,   $right , $top)"
}
