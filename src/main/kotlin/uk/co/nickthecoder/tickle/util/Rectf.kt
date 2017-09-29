package uk.co.nickthecoder.tickle.util

/**
 * A rectangle using floats
 */
data class Rectf(
        var left: Float,
        var bottom: Float,
        var right: Float,
        var top: Float) {

    val width = right - left
    val height = top - bottom

}
