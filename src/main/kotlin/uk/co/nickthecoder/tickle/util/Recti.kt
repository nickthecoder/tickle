package uk.co.nickthecoder.tickle.util

/**
 * A rectangle using Ints
 */
data class Recti(
        var left: Int,
        var bottom: Int,
        var right: Int,
        var top: Int) {

    val width = right - left
    val height = top - bottom
    val topDownHeight = bottom - top
}
