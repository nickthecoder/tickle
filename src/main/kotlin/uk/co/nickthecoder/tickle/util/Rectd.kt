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

    constructor() : this(0.0, 0.0, 0.0, 0.0)

    constructor(other: Rectd) : this(other.left, other.bottom, other.right, other.top)

    fun plus(dx: Double, dy: Double, dest: Rectd = this): Rectd {
        dest.left = left + dx
        dest.right = right + dx
        dest.top = top + dy
        dest.bottom = bottom + dy
        return dest
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Rectd) {
            return false
        }
        return other.left == left && other.bottom == bottom && other.right == right && other.top == top
    }

    override fun toString(): String = "($left , $bottom   ,   $right , $top)"
}
